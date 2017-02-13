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
import nl.rakis.fs.AircraftInfo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Aircraft.
 */
@RequestScoped
public class Aircraft {

    private static final Logger log = Logger.getLogger(Aircraft.class.getName());

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

    public AircraftInfo getAircraftInSession(String callsign, String session)
    {
        log.finest("getAircraftInSession(\"" + callsign + "\", \"" + session + "\")");
        AircraftInfo result = null;
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            String value = cmd.get(AircraftInfo.getType()+":"+session+":"+callsign);
            if (value != null) {
                result = AircraftInfo.fromString(value);
                log.finest("getAircraftInSession(): Found");
            }
        }
        log.finest("getAircraftInSession(): Done");
        return result;
    }

    public void setAircraftInSession(AircraftInfo aircraft, String session) {
        log.finest("setAircraftInSession(\"" + aircraft.getAtcId() + "\", \"" + session + "\")");
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            final String key = aircraft.getKey(session);
            log.finest("setAircraftInSession(): Storing aircraft with key \"" + key + "\"");

            cmd.set(key, aircraft.toString());
        }
        log.finest("setAircraftInSession(): Done");
    }

    public List<AircraftInfo> getAllAircraftInSession(String session) {
        log.finest("getAllAircraftInSession(\"" + session + "\")");
        List<AircraftInfo> result = new ArrayList<>();
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();
            List<String> allKeys = new ArrayList<>();

            final String match = AircraftInfo.getType() + ":" + session + ":*";
            log.finest("getAllAircraftInSession(): Scanning for keys matching\"" + match + "\")");

            ScanArgs sa = new ScanArgs();
            sa.match(match);
            sa.limit(1024);
            KeyScanCursor<String> cursor = cmd.scan(sa);
            allKeys.addAll(cursor.getKeys());
            while (!cursor.isFinished()) {
                cursor = cmd.scan(cursor, sa);
                allKeys.addAll(cursor.getKeys());
            }
            log.finest("getAircraftInSession(): " + allKeys.size() + " key(s) found");

            for (String key: allKeys) {
                String value = cmd.get(key);
                if (value != null) {
                    result.add(AircraftInfo.fromString(value));
                }
            }
        }
        log.finest("getAircraftInSession(): " + result.size() + " aircraft returned");
        return result;
    }

    public void removeAircraftFromSession(String callsign, String session) {
        log.finest("removeAircraftFromSession(\"" + callsign + "\", \"" + session + "\")");
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            final String key = AircraftInfo.getType() + ":" + session + ":" + callsign;
            log.finest("setAircraftInSession(): Removing aircraft with key \"" + key + "\"");

            cmd.del(key);
        }
        log.finest("removeAircraftFromSession(): Done");
    }
}
