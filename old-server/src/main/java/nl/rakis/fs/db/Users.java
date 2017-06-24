/*
 * Copyright 2016, 2017 Bert Laverman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package nl.rakis.fs.db;

import com.lambdaworks.redis.KeyScanCursor;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.ScanArgs;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import nl.rakis.fs.UserInfo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Store and/or retrieve users.
 */
@RequestScoped
public class Users
{

    public static final String SCRUBBED_PASSWORD = "***";
    RedisClient rc;

    @PostConstruct
    public void init()
    {
        rc = SetupDB.getRdc();
    }

    public UserInfo getUser(String username) {
        UserInfo result = null;
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            String value = cmd.get(UserInfo.getType()+":"+username);
            if (value != null) {
                result = UserInfo.fromString(value);
            }
        }
        return result;
    }

    public static UserInfo scrubUser(UserInfo user) {
        user.setPassword(SCRUBBED_PASSWORD);
        return new UserInfo(user.getUsername(), SCRUBBED_PASSWORD, user.getSession());
    }

    public UserInfo getUserScrubbed(String username) {
        return scrubUser(getUser(username));
    }

    public void setUser(UserInfo user) {
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            cmd.set(user.getKey(), user.toString());
        }
    }

    public List<UserInfo> getAllUsers() {
        List<UserInfo> result = new ArrayList<>();
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();
            List<String> allKeys = new ArrayList<>();

            ScanArgs sa = new ScanArgs();
            sa.match(UserInfo.USER_TYPE+":*");
            sa.limit(1024);
            KeyScanCursor<String> cursor = cmd.scan(sa);
            allKeys.addAll(cursor.getKeys());
            while (!cursor.isFinished()) {
                cursor = cmd.scan(cursor, sa);
                allKeys.addAll(cursor.getKeys());
            }

            for (String key: allKeys) {
                String value = cmd.get(key);
                if (value != null) {
                    result.add(UserInfo.fromString(value));
                }
            }
        }
        return result;
    }

    public List<UserInfo> getAllUsersScrubbed() {
        return getAllUsers().stream().map(Users::scrubUser).collect(Collectors.toList());
    }

    @PreDestroy
    public void cleanup()
    {
        rc.shutdown();
        rc = null;
    }
}
