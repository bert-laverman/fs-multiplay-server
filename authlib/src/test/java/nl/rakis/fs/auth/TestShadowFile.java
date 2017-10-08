package nl.rakis.fs.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

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
        String pwd = shadow.getPasswordHash(user);

        Assert.assertNotNull("Reading password from shadow", pwd);
        try {
            PasswordStorage.verifyPassword("password", pwd);
        } catch (PasswordStorage.CannotPerformOperationException e) {
            Assert.fail("Failed to create hash");
        } catch (PasswordStorage.InvalidHashException e) {
            Assert.fail("Test hash did not match");
        }
    }

    @Test
    public void testSetPassword()
    {
        try {
            File tmp = File.createTempFile("junit_", ".tmp");
            FileUtil.createFile(tmp, "");
            ShadowFile tmpShdw = new ShadowFile(tmp);

            Assert.assertEquals("Empty file should have no users", 0, tmpShdw.getPasswords().size());
            Assert.assertTrue(tmpShdw.setPassword("testUser", "password"));
            Assert.assertEquals("Adding a user should result in a count of 1", 1, tmpShdw.getPasswords().size());
            Assert.assertNotNull("User \"testUser\" should now have a password", tmpShdw.getPasswordHash("testUser"));
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }
}
