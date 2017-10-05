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

public class PasswordFile {

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

    private String path;
    private HashMap<String,User> users = new HashMap<>();

    public PasswordFile(String path)
    {
        log.debug("PasswordFile(\"" + path + "\")");

        this.path = path;
    }

    private void checkEmpty(String fldName, String fld) throws IOException {
        if ((fld != null) && (fld.length() > 0) && !fld.equals("x") && !fld.equals("*")) {
            throw new IOException(fldName + " should be empty: \"" + fld + "\"");
        }
    }

    private void checkNonEmpty(String fldName, String fld) throws IOException {
        if ((fld == null) || (fld.length() == 0)) {
            throw new IOException(fldName + " should not be empty: \"" + fld + "\"");
        }
    }

    private void load()
    {
        log.trace("load()");

        if (users.size() == 0) {
            if (log.isDebugEnabled()) {
                log.debug("load(): Reading \"" + path + "\"");
            }
            try {
                try (FileReader fr = new FileReader(path);
                     BufferedReader br=new BufferedReader(fr))
                {
                    if (log.isTraceEnabled()) {
                        log.trace("load(): Userlist \"" + path + "\" opened");
                    }
                    for (String line = br.readLine(); line != null; line = br.readLine()) {
                        if (log.isTraceEnabled()) {
                            log.trace("load(): Read \"" + line + "\"");
                        }

                        String[] pwdFields = line.split(":");
                        if ((pwdFields == null) || (pwdFields.length != NUM_FIELDS))
                        {
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
                }
            }
            catch (IOException e) {
                users.clear();

                log.error("load(): Failed to load \"" + path + "\"", e);
            }
            if (log.isDebugEnabled()) {
                log.debug("load(): " + users.size() + " user(s) read");
            }
        }
    }

    public Map<String,User> getUsers() {
        load();
        return users;
    }

    public User getUser(String userId) {
        return getUsers().containsKey(userId) ? users.get(userId) : null;
    }
}
