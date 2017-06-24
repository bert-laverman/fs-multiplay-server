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

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import nl.rakis.fs.SessionInfo;
import nl.rakis.fs.UserInfo;
import nl.rakis.fs.security.PasswordStorage;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;

/**
 * Fill the database if empty
 */
public class SetupDB {

    public static final String INIT_DONE = "initDone";
    public static final String USER_SESSION_CACHE = "UserSessionCache";

    public static RedisClient getRdc() {
        return RedisClient.create("redis://redis:6379/0");
    }

    static {
        RedisClient rc = getRdc();
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            if (cmd.setnx(INIT_DONE, "true")) {
                SessionInfo session = new SessionInfo(SessionInfo.ADMIN_SESSION, "Dummy session for admin users");
                cmd.set(session.getKey(), session.toString());

                session = new SessionInfo(SessionInfo.DUMMY_SESSION, "Dummy session for normal users");
                cmd.set(session.getKey(), session.toString());

                UserInfo user = new UserInfo(UserInfo.ADMIN_USER, PasswordStorage.createHash("admin"));
                user.setSession(SessionInfo.DUMMY_SESSION);
                cmd.set(user.getKey(), user.toString());
            }
        } catch (PasswordStorage.CannotPerformOperationException e) {
            throw new RuntimeException(e);
        }
        rc.shutdown();
    }

    public static Cache<String,String> getCacheManager() {
        return Caching.getCache(USER_SESSION_CACHE, String.class, String.class);
    }

    static {
        CacheManager mgr = Caching.getCachingProvider().getCacheManager();
        MutableConfiguration<String,String> conf = new MutableConfiguration<>();
        conf.setTypes(String.class, String.class);
        try {
        /*Cache<String,String> cache = */
            mgr.createCache(USER_SESSION_CACHE, conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
