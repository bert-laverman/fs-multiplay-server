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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ShadowFile extends SecurityFile {

    private static final Logger log = LogManager.getLogger(ShadowFile.class);

    public static final int NUM_FIELDS = 2;
    public static final String FLDNAME_USERNAME = "Username";
    public static final int FLD_USERNAME = 0;
    public static final String FLDNAME_PASSWORD = "Password";
    public static final int FLD_PASSWORD = 1;

    private String path;
    private HashMap<String,String> passwords = new HashMap<>();

    public ShadowFile(String path)
    {
        log.debug("ShadowFile(\"" + path + "\")");

        this.path = path;
    }

    private void load()
    {
        log.trace("load()");

        if (passwords.size() == 0) {
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
                }
            }
            catch (IOException e) {
                passwords.clear();

                log.error("load(): Failed to load \"" + path + "\"", e);
            }
            if (log.isDebugEnabled()) {
                log.debug("load(): " + passwords.size() + " password(s) read");
            }
        }
    }

    public Map<String,String> getPasswords() {
        load();
        return passwords;
    }

    public String getPassword(String userId) {
        return getPasswords().containsKey(userId) ? passwords.get(userId) : null;
    }
}
