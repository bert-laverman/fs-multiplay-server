package nl.rakis.fs.db;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import nl.rakis.fs.SessionInfo;
import nl.rakis.fs.UserInfo;
import nl.rakis.fs.security.PasswordStorage;

/**
 * Fill the database if empty
 */
public class SetupDB {

    public static final String INIT_DONE = "initDone";

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
}
