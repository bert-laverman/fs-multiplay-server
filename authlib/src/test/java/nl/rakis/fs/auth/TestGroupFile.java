package nl.rakis.fs.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class TestGroupFile {

    public static final String TEST_GROUP = "testGroup";
    public static final String TEST_USER = "testUser";

    private static Logger log = LogManager.getLogger(TestGroupFile.class);

    GroupFile grp;

    @Before
    public void setup()
    {
        String pathName = new File(getClass().getResource("group").getFile()).getAbsolutePath();
        log.debug("pathName = \"" + pathName + "\"");
        grp = new GroupFile(pathName);
    }

    @Test
    public void testGetGroup()
    {
        Assert.assertEquals("Reading group file", 1, grp.getGroups().size());
        Assert.assertEquals("Reading group from group", 1, grp.getUsersInGroup(TEST_GROUP).size());

    }

    @Test
    public void testIsUserInGroup()
    {
        Assert.assertTrue("Testing if testUser is in testGroup", grp.isUserInGroup(TEST_USER, TEST_GROUP));
        Assert.assertFalse("Testing if testUser is not in nonsenseGroup", grp.isUserInGroup(TEST_USER, "nonsenseGroup"));
    }
}
