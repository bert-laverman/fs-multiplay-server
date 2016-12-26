package nl.rakis.fs.db;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import nl.rakis.fs.SessionInfo;
import nl.rakis.fs.UserData;
import nl.rakis.fs.UserInfo;
import nl.rakis.fs.security.PasswordStorage;

import java.util.UUID;

/**
 * Created by bertl on 12/25/2016.
 */
public class SetupDB {

    static {
        RedisClient rc = RedisClient.create("redis://smb:6379/0");
        StatefulRedisConnection<String,String> connection = rc.connect();
        RedisCommands<String,String> cmd = connection.sync();

        if (cmd.setnx("initDone", "true")) {
            try {
                SessionInfo session = new SessionInfo("Admin Session", "Dummy session for admin users");
                cmd.hmset(session.getType()+":"+session.getId(), session.asMap());

                session = new SessionInfo("Waiting Room Session", "Dummy session for normal users");
                cmd.hmset(session.getType()+":"+session.getId(), session.asMap());

                UserInfo user = new UserInfo("admin", PasswordStorage.createHash("admin"));
                cmd.hmset(session.getType()+":"+user.getId(), user.asMap());
            } catch (PasswordStorage.CannotPerformOperationException e) {
                throw new RuntimeException(e);
            }
        }
        connection.close();
        rc.shutdown();
    }
}
