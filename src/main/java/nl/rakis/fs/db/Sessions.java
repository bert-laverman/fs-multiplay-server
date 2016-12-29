package nl.rakis.fs.db;

import com.lambdaworks.redis.KeyScanCursor;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.ScanArgs;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import nl.rakis.fs.SessionInfo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import java.util.ArrayList;
import java.util.List;

/**
 * Retrieve and store Sessions
 */
@RequestScoped
public class Sessions {

    RedisClient rc;

    @PostConstruct
    public void init()
    {
        rc = SetupDB.getRdc();
    }

    public SessionInfo getSession(String name) {
        SessionInfo result = null;
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            String value = cmd.get(SessionInfo.getType()+":"+name);
            if (value != null) {
                result = SessionInfo.fromString(value);
            }
        }
        return result;
    }

    public void setSession(SessionInfo session) {
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            cmd.set(SessionInfo.getType()+":"+session.getName(), session.toString());
        }
    }

    public List<SessionInfo> getAllSessions() {
        List<SessionInfo> result = new ArrayList<>();
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();
            List<String> allKeys = new ArrayList<>();

            ScanArgs sa = new ScanArgs();
            sa.match(SessionInfo.SESSION_TYPE+":*");
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
                    result.add(SessionInfo.fromString(value));
                }
            }
        }
        return result;
    }

    public void delete(String name) {
        try (StatefulRedisConnection<String,String> connection = rc.connect()) {
            RedisCommands<String,String> cmd = connection.sync();

            cmd.del(SessionInfo.getType()+":"+name);
        }
    }

    @PreDestroy
    public void cleanup()
    {
        rc.shutdown();
        rc = null;
    }
}
