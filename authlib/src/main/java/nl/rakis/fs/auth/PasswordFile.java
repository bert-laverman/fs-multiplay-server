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

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class PasswordFile extends SecurityFile {

    private static final Logger log = LogManager.getLogger(PasswordFile.class);

    public static final int NUM_FIELDS = 7;
    public static final String FLDNAME_USERNAME = "Username";
    public static final int FLD_USERNAME = 0;
    public static final String FLDNAME_PASSWORD = "Password";
    public static final int FLD_PASSWORD = 1;
    public static final String FLDNAME_USERID = "User ID";
    public static final int FLD_USERID = 2;
    public static final String FLDNAME_GROUPID = "Group ID";
    public static final int FLD_GROUPID = 3;
    public static final String FLDNAME_REALNAME = "Real name";
    public static final int FLD_REALNAME = 4;
    public static final String FLDNAME_HOME = "Home directory";
    public static final int FLD_HOME = 5;
    public static final String FLDNAME_SHELL = "Shell";
    public static final int FLD_SHELL = 6;

    private File path;
    private HashMap<String,User> users = new HashMap<>();

    public PasswordFile(String path)
    {
        log.debug("PasswordFile(\"" + path + "\")");

        this.path = new File(path);
    }

    private void load() {
        if (log.isDebugEnabled()) {
            log.debug("load()");
        }

        load(path, (String line) -> {
            boolean result = false;

            try {
                String[] pwdFields = line.split(":");
                if (pwdFields.length != NUM_FIELDS) {
                        throw new IOException("Bad passwd format: \"" + line + "\"");
                }
                User user = new User();

                checkNonEmpty(FLDNAME_USERNAME, pwdFields [FLD_USERNAME]);
                user.userName = pwdFields [FLD_USERNAME];
                checkEmpty(FLDNAME_PASSWORD, pwdFields [FLD_PASSWORD]);
                checkEmpty(FLDNAME_USERID, pwdFields [FLD_USERID]);
                checkEmpty(FLDNAME_GROUPID, pwdFields [FLD_GROUPID]);
                checkNonEmpty(FLDNAME_REALNAME, pwdFields [FLD_REALNAME]);
                user.longName = pwdFields [FLD_REALNAME];
                checkNonEmpty(FLDNAME_HOME, pwdFields [FLD_HOME]);
                user.defaultSession = pwdFields [FLD_HOME];
                checkEmpty(FLDNAME_SHELL, pwdFields [FLD_SHELL]);

                if (log.isDebugEnabled()) {
                    log.debug("load(): Adding user \"" + user.userName + "\"");
                }
                users.put(user.userName, user);
            }
            catch (IOException e) {
                log.error("load(): Exception while loading \"" + path + "\"", e);
                result = true;
                users.clear();
            }
            return result;
        });
        if (log.isDebugEnabled()) {
            log.debug("load(): " + users.size() + " user(s) read");
        }
    }

    private boolean store() {
        return store("users", path, (PrintWriter pr) -> {
            for (User user: users.values()) {
                pr.print(user.userName);
                pr.print(":x:x:x:");
                pr.print(user.longName);
                pr.print(":");
                pr.print(user.defaultSession);
                pr.println(":x");
            }
        });
    }

    public Map<String,User> getUsers() {
        load();
        return users;
    }

    public User getUser(String userId) {
        return getUsers().containsKey(userId) ? users.get(userId) : null;
    }

    public boolean addUser(String userName, String longName, String defaultSession) {
        if (log.isInfoEnabled()) {
            log.info("addUser(): Adding user \"" + userName + "\", real name \"" + longName + "\", default session \"" + defaultSession + "\"");
        }

        boolean result = false;

        if (getUser(userName) != null) {
            log.error("addUser(): Tried to add an existing user \"" + userName + "\"");
        }
        else {
            User user = new User(userName, longName, defaultSession);
            users.put(userName, user);
            result = store();
        }
        if (log.isInfoEnabled()) {
            log.info("addUser(): " + (result ? "Success" : "Failed"));
        }
        return result;
    }

    public boolean removeUser(String userName) {
        if (log.isInfoEnabled()) {
            log.info("removeUser(): Removing user \"" + userName + "\"");
        }

        boolean result = false;

        if (getUser(userName) == null) {
            log.error("removeUser(): Tried to remove a non-existing user \"" + userName + "\"");
        }
        else {
            users.remove(userName);
            result = store();
        }
        if (log.isInfoEnabled()) {
            log.info("removeUser(): " + (result ? "Success" : "Failed"));
        }
        return result;
    }
}
