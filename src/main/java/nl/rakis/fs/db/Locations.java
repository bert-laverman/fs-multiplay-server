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
        log.info("getLocation(\"" + callsign + "\", \"" + session + "\")");
        LocationInfo result = null;
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            String value = cmd.get(LocationInfo.getType()+":"+session+":"+callsign);
            if (value != null) {
                result = LocationInfo.fromString(value);
                log.info("getLocation(): Found");
            }
        }
        log.info("getLocation(): Done");
        return result;
    }

    public List<LocationInfo> getAll(String session)
    {
        log.info("getAll(\"" + session + "\")");
        List<LocationInfo> result = new ArrayList<>();
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();
            List<String> allKeys = new ArrayList<>();

            final String match = LocationInfo.getType() + ":" + session + ":*";
            log.info("getAll(): Scanning for keys matching\"" + match + "\")");

            ScanArgs sa = new ScanArgs();
            sa.match(match);
            sa.limit(1024);
            KeyScanCursor<String> cursor = cmd.scan(sa);
            allKeys.addAll(cursor.getKeys());
            while (!cursor.isFinished()) {
                cursor = cmd.scan(cursor, sa);
                allKeys.addAll(cursor.getKeys());
            }
            log.info("getAll(): " + allKeys.size() + " key(s) found");

            for (String key: allKeys) {
                String value = cmd.get(key);
                if (value != null) {
                    log.info("getAll(): Found " + value);
                    result.add(LocationInfo.fromString(value));
                }
            }
        }
        log.info("getAll(): " + result.size() + " locations returned");
        return result;
    }

    public void setLocation(LocationInfo location, String callsign, String session) {
        log.info("setLocation(..., \"" + callsign + "\", \"" + session + "\")");
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            final String key = location.getKey(session, callsign);
            log.info("setLocation(): Storing location with key \"" + key + "\"");

            cmd.set(key, location.toString());
        }
        log.info("setLocation(): Done");
    }

    public void removeLocation(String callsign, String session) {
        log.info("removeLocation(\"" + callsign + "\", \"" + session + "\")");
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            final String key = LocationInfo.getType() + ":" + session + ":" + callsign;
            log.info("removeLocation(): Removing location with key \"" + key + "\"");

            cmd.del(key);
        }
        log.info("removeLocation(): Done");
    }

}
