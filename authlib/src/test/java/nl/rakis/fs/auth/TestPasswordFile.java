/*
 * Copyright 2017 Bert Laverman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package nl.rakis.fs.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class TestPasswordFile
{

    private static Logger log = LogManager.getLogger(TestPasswordFile.class);

    private PasswordFile pw;

    @Before
    public void setup()
    {
        String pathName = new File(getClass().getResource("passwd").getFile()).getAbsolutePath();
        log.debug("pathName = \"" + pathName + "\"");
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

    @Test
    public void testAddUser()
    {
        try {
            File tmp = File.createTempFile("junit_", ".tmp");
            FileUtil.createFile(tmp, "");
            PasswordFile tmpPw = new PasswordFile(tmp.getAbsolutePath());

            Assert.assertEquals("Empty file should have no users", 0, tmpPw.getUsers().size());
            Assert.assertTrue(tmpPw.addUser("testUser", "Test User", "Default Session"));
            Assert.assertEquals("Adding a user should result in a count of 1", 1, tmpPw.getUsers().size());
            Assert.assertNotNull("User \"testUser\" should now exist", tmpPw.getUser("testUser"));
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testRemoveUser()
    {
        try {
            File tmp = File.createTempFile("junit_", ".tmp");
            FileUtil.createFile(tmp, "testUser:x:x:x:Test User:Test Session:x\n");
            PasswordFile tmpPw = new PasswordFile(tmp.getAbsolutePath());

            Assert.assertEquals("User file should have 1 users", 1, tmpPw.getUsers().size());
            Assert.assertTrue(tmpPw.removeUser("testUser"));
            Assert.assertEquals("Remove a user should result in a count of 0", 0, tmpPw.getUsers().size());
            Assert.assertNull("User \"testUser\" should no longer exist", tmpPw.getUser("testUser"));
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }
}
