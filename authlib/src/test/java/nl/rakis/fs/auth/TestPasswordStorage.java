package nl.rakis.fs.auth;

import org.junit.Assert;
import org.junit.Test;

public class TestPasswordStorage {

    @Test
    public void testCreateHash()
    {
        String password = "password";
        final String hash;
        try {
            hash = PasswordStorage.createHash(password);
            PasswordStorage.verifyPassword(password, hash);
        } catch (PasswordStorage.CannotPerformOperationException e) {
            Assert.fail("Failed to create hash");
        } catch (PasswordStorage.InvalidHashException e) {
            Assert.fail("Failed to verify hash");
        }
    }
}
