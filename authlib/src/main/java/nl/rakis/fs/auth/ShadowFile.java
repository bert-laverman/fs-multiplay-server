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

public class ShadowFile extends SecurityFile {

    private static final Logger log = LogManager.getLogger(ShadowFile.class);

    public static final int NUM_FIELDS = 2;
    public static final String FLDNAME_USERNAME = "Username";
    public static final int FLD_USERNAME = 0;
    public static final String FLDNAME_PASSWORD = "Password";
    public static final int FLD_PASSWORD = 1;

    private File path;
    private HashMap<String,String> passwords = new HashMap<>();

    public ShadowFile(String path)
    {
        log.debug("ShadowFile(\"" + path + "\")");

        this.path = new File(path);
    }

    public ShadowFile(File path)
    {
        log.debug("ShadowFile(\"" + path.getAbsolutePath() + "\")");

        this.path = path;
    }

    private void load() {
        if (log.isDebugEnabled()) {
            log.debug("load()");
        }

        load(path, (String line) -> {
            boolean result = false;

            if (log.isTraceEnabled()) {
                log.trace("load(): Read \"" + line + "\"");
            }

            try {
                int pos = line.indexOf(':');
                if ((pos <= 0) || (pos == line.length()-1)) {
                    throw new IOException("Bad passwd format: \"" + line + "\"");
                }

                final String user = line.substring(0, pos);
                checkNonEmpty(FLDNAME_USERNAME, user);
                final String pwd = line.substring(pos+1);
                checkNonEmpty(FLDNAME_PASSWORD, pwd);

                if (log.isDebugEnabled()) {
                    log.debug("load(): Adding password for user \"" + user + "\"");
                }
                passwords.put(user, pwd);
            }
            catch (IOException e) {
                log.error("load(): Exception while loading \"" + path + "\"", e);
                result = true;
                passwords.clear();
            }
             return result;
        });
        if (log.isDebugEnabled()) {
            log.debug("load(): " + passwords.size() + " password(s) read");
        }
    }

    private boolean store() {
        return store("passwords", path, (PrintWriter pr) -> {
            for (String user: passwords.keySet()) {
                pr.print(user);
                pr.print(":");
                pr.print(passwords.get(user));
            }
        });
    }

    public Map<String,String> getPasswords() {
        load();
        return passwords;
    }

    public String getPasswordHash(String userId) {
        return getPasswords().containsKey(userId) ? passwords.get(userId) : null;
    }

    public boolean setPassword(String userName, String password) {
        if (log.isInfoEnabled()) {
            log.info("setPassword(): Setting password for user \"" + userName + "\"");
        }

        boolean result = false;

        if (getPasswordHash(userName) == null) {
            log.info("setPassword(): Adding new user to password list");
        }
        try {
            passwords.put(userName, PasswordStorage.createHash(password));
            result = store();
        }
        catch (PasswordStorage.CannotPerformOperationException e) {
            log.error("setPassword(): Failed to create hash", e);
        }

        if (log.isInfoEnabled()) {
            log.info("setPassword(): " + (result ? "Success" : "Failed"));
        }
        return result;
    }


    public boolean removePassword(String userName) {
        if (log.isInfoEnabled()) {
            log.info("removePassword(): Removing password for user \"" + userName + "\"");
        }

        boolean result = false;

        if (passwords.containsKey(userName)) {
            passwords.remove(userName);
            result = store();
        }
        else {
            log.error("removePassword(): User \"" + userName + "\" does not exist.");
        }
        if (log.isInfoEnabled()) {
            log.info("removePassword(): " + (result ? "Success" : "Failed"));
        }
        return result;
    }
}
