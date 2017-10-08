package nl.rakis.fs.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

public class TestGroupFile {

    public static final String TEST_GROUP = "testGroup";
    public static final String TEST_USER = "testUser";

    private static Logger log = LogManager.getLogger(TestGroupFile.class);

    GroupFile grp;

    @Before
    public void setup()
    {
        String pathName = new File(getClass().getResource("group").getFile()).getAbsolutePath();
        grp = new GroupFile(pathName);
    }

    @Test
    public void testGetGroup()
    {
        log.info("testGetGroup(): ### Start test");

        Assert.assertEquals("Reading group file", 1, grp.getGroups().size());
        Assert.assertEquals("Reading group from group", 1, grp.getUsersInGroup(TEST_GROUP).size());

        log.info("testGetGroup(): ### Test done");
    }

    @Test
    public void testIsUserInGroup()
    {
        log.info("testIsUserInGroup(): ### Start test");
        
        Assert.assertTrue("Testing if testUser is in testGroup", grp.isUserInGroup(TEST_USER, TEST_GROUP));
        Assert.assertFalse("Testing if testUser is not in nonsenseGroup", grp.isUserInGroup(TEST_USER, "nonsenseGroup"));

        log.info("testIsUserInGroup(): ### Test done");
    }

    @Test
    public void testAddUserToGroup()
    {
        log.info("testAddUserToGroup(): ### Start test");
        
        try {
            File tmp = File.createTempFile("junit_", ".tmp");
            FileUtil.createFile(tmp, "");
            GroupFile tmpGrp = new GroupFile(tmp.getAbsolutePath());

            Assert.assertEquals("Empty file should have no groups", 0, tmpGrp.getGroups().size());
            Assert.assertTrue(tmpGrp.addUserToGroup("testUser", "testGroup"));
            Assert.assertEquals("Adding a user to group should result in a count of 1", 1, tmpGrp.getGroups().size());
            Assert.assertNotNull("Group \"testGroup\" should now exist", tmpGrp.getGroups().containsKey("testGroup"));
            Assert.assertTrue("User \"testUser\" should now be in group \"testGroup\"", tmpGrp.isUserInGroup("testUser", "testGroup"));
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

        log.info("testAddUserToGroup(): ### Test done");
    }

    @Test
    public void testRemoveUserFromGroup()
    {
        log.info("testRemoveUserFromGroup(): ### Start test");
        
        try {
            File tmp = File.createTempFile("junit_", ".tmp");
            FileUtil.createFile(tmp, "testGroup:x:x:testUser\n");
            GroupFile tmpGrp = new GroupFile(tmp.getAbsolutePath());

            Assert.assertEquals("Group file should have 1 group", 1, tmpGrp.getGroups().size());
            Assert.assertTrue(tmpGrp.removeUserFromGroup("testUser", "testGroup"));
            Assert.assertEquals("Removing user \"testUser\" from \"testGroup\" should still result in a count of 1", 1, tmpGrp.getGroups().size());
            Assert.assertFalse("User \"testUser\" should no longer be in group \"testGroup\"", tmpGrp.isUserInGroup("testUser", "testGroup"));
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

        log.info("testRemoveUserFromGroup(): ### Test done");
    }
}
