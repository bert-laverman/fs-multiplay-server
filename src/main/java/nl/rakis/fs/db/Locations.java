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
import nl.rakis.fs.LocationInfo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Aircraft locations in a session.
 */
@RequestScoped
public class Locations
{
    private static final Logger log = Logger.getLogger(Locations.class.getName());

    RedisClient rc;

    @PostConstruct
    public void init()
    {
        rc = SetupDB.getRdc();
    }

    @PreDestroy
    public void cleanup() {
        rc.shutdown();
        rc = null;
    }

    public LocationInfo getLocation(String callsign, String session)
    {
        log.finest("getLocation(\"" + callsign + "\", \"" + session + "\")");
        LocationInfo result = null;
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            String value = cmd.get(LocationInfo.getType()+":"+session+":"+callsign);
            if (value != null) {
                result = LocationInfo.fromString(value);
                log.finest("getLocation(): Found");
            }
        }
        log.finest("getLocation(): Done");
        return result;
    }

    public List<LocationInfo> getAll(String session)
    {
        log.finest("getAll(\"" + session + "\")");
        List<LocationInfo> result = new ArrayList<>();
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();
            List<String> allKeys = new ArrayList<>();

            final String match = LocationInfo.getType() + ":" + session + ":*";
            log.finest("getAll(): Scanning for keys matching\"" + match + "\")");

            ScanArgs sa = new ScanArgs();
            sa.match(match);
            sa.limit(1024);
            KeyScanCursor<String> cursor = cmd.scan(sa);
            allKeys.addAll(cursor.getKeys());
            while (!cursor.isFinished()) {
                cursor = cmd.scan(cursor, sa);
                allKeys.addAll(cursor.getKeys());
            }
            log.finest("getAll(): " + allKeys.size() + " key(s) found");

            for (String key: allKeys) {
                String value = cmd.get(key);
                if (value != null) {
                    log.finest("getAll(): Found " + value);
                    result.add(LocationInfo.fromString(value));
                }
            }
        }
        log.finest("getAll(): " + result.size() + " locations returned");
        return result;
    }

    public void setLocation(LocationInfo location, String callsign, String session) {
        log.finest("setLocation(..., \"" + callsign + "\", \"" + session + "\")");
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            final String key = location.getKey(session, callsign);
            log.finest("setLocation(): Storing location with key \"" + key + "\"");

            cmd.set(key, location.toString());
        }
        log.finest("setLocation(): Done");
    }

    public void removeLocation(String callsign, String session) {
        log.finest("removeLocation(\"" + callsign + "\", \"" + session + "\")");
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            final String key = LocationInfo.getType() + ":" + session + ":" + callsign;
            log.finest("removeLocation(): Removing location with key \"" + key + "\"");

            cmd.del(key);
        }
        log.finest("removeLocation(): Done");
    }

}
