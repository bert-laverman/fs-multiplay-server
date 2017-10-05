package nl.rakis.fs.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class TestShadowFile
{

    private static Logger log = LogManager.getLogger(TestShadowFile.class);

    ShadowFile shadow;

    @Before
    public void setup()
    {
        String pathName = new File(getClass().getResource("shadow").getFile()).getAbsolutePath();
        log.debug("pathName = \"" + pathName + "\"");
        shadow = new ShadowFile(pathName);
    }

    @Test
    public void testGetPassword()
    {
        Assert.assertEquals("Reading shadow file", 1, shadow.getPasswords().size());

        final String user = "testUser";
        String pwd = shadow.getPassword(user);

        Assert.assertNotNull("Reading password from shadow", pwd);
        try {
            PasswordStorage.verifyPassword("password", pwd);
        } catch (PasswordStorage.CannotPerformOperationException e) {
            Assert.fail("Failed to create hash");
        } catch (PasswordStorage.InvalidHashException e) {
            Assert.fail("Test hash did not match");
        }
    }
}
