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
import nl.rakis.fs.EngineInfo;
import nl.rakis.fs.FSKeylessData;
import nl.rakis.fs.LocationInfo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import java.util.logging.Logger;

/**
 * Aircraft subsidiary info.
 */
@ApplicationScoped
public class Extras
{
    private static final Logger log = Logger.getLogger(Extras.class.getName());

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

    public <T extends FSKeylessData> T get(String session, String callsign, Class<T> clazz)
    {
        log.finest("get(\"" + callsign + "\", \"" + session + "\", "+clazz.getCanonicalName()+")");
        T result = null;
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            try {
                result = clazz.newInstance();
                final String key = result.getKey(session, callsign);

                final String value = cmd.get(key);
                if (value != null) {
                    log.finest("get(): key=\"" + key + "\", value=\"" + value + "\"");
                    result.parse(value);
                }
                else {
                    log.finest("get(): key=\"" + key + "\", Not found");
                    result = null;
                }
            } catch (Exception e) {
                log.severe(e.getLocalizedMessage());
            }
        }
        log.finest("get(): Done");
        return result;
    }

    public <T extends FSKeylessData> void set(T obj, String session, String callsign) {
        log.finest("set(..., \"" + callsign + "\", \"" + session + "\")");
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            final String key = obj.getKey(session, callsign);
            final String value = obj.toString();

            log.finest("set(): key=\"" + key + "\", value=\"" + value + "\"");
            cmd.set(key, value);
        }
        log.finest("set(): Done");
    }

    public <T extends FSKeylessData> void set(String jsonObj, String session, String callsign, Class<T> clazz) {
        log.finest("set(..., \"" + callsign + "\", \"" + session + "\")");
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            final T obj = clazz.newInstance(); // only to be able to do the next one.
            final String key = obj.getKey(session, callsign);

            cmd.set(key, jsonObj);
        } catch (Exception e) {
            log.severe(e.getLocalizedMessage());
        }
        log.finest("set(): Done");
    }

    public <T extends FSKeylessData> void remove(T obj, String session, String callsign) {
        log.finest("remove(\"" + callsign + "\", \"" + session + "\")");
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            final String key = obj.getKey(session, callsign);

            cmd.del(key);
        }
        log.finest("remove(): Done");
    }

    public <T extends FSKeylessData> void remove(String session, String callsign, Class<T> clazz) {
        log.finest("remove(\"" + callsign + "\", \"" + session + "\")");
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            final T obj = clazz.newInstance();
            final String key = obj.getKey(session, callsign);

            cmd.del(key);
        } catch (Exception e) {
            log.severe(e.getLocalizedMessage());
        }
        log.finest("remove(): Done");
    }

}
