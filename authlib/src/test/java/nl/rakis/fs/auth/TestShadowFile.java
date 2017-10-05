package nl.rakis.fs.auth;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class TestPasswordFile
{

    PasswordFile pw;

    @Before
    public void setup()
    {
        String pathName = new File(getClass().getResource("passwd").getFile()).getAbsolutePath();
        System.err.println("pathName = \"" + pathName + "\"");
        pw = new PasswordFile(pathName);
    }

    @Test
    public void testGetUser()
    {
        Assert.assertEquals("Reading passwd file", 1, pw.getUsers().size());

        User user = pw.getUser("testUser");

        Assert.assertNotNull("Reading user from passwd", user.userName);
        Assert.assertEquals("Reading userName from passwd", "testUser", user.userName);
        Assert.assertEquals("Reading longName from passwd", "Test User", user.longName);
        Assert.assertEquals("Reading defaultSession from passwd", "Test Session", user.defaultSession);
    }
}
