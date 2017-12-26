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

import nl.rakis.fs.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;

@Singleton
public class AuthFileManager
{

    public static final String CFG_AUTH_DIR = "nl.rakis.fs.auth.dir";
    public static final String DEF_AUTH_DIR = "/opt/fsmultiplay/etc";

    private static final Logger log = LogManager.getLogger(AuthFileManager.class);

    public static final String CFG_PASSWD = "nl.rakis.fs.auth.passwd";
    public static final String DEF_PASSWD = "passwd";
    public static final String CFG_SHADOW = "nl.rakis.fs.auth.shadow";
    public static final String DEF_SHADOW = "shadow";
    public static final String CFG_GROUP = "nl.rakis.fs.auth.group";
    public static final String DEF_GROUP = "group";

    @Inject
    private Config cfg;

    private File dAuth;
    private File fPasswd;
    private File fShadow;
    private File fGroup;
    private PasswordFile passwd;
    private ShadowFile shadow;
    private GroupFile group;

    public AuthFileManager() {
        log.debug("AuthFileManager()");

        log.debug("AuthFileManager(): Done");
    }

    @PostConstruct
    private void init() {
        log.debug("init()");

        dAuth = new File(cfg.get(CFG_AUTH_DIR, DEF_AUTH_DIR));
        if (log.isInfoEnabled()) {
            log.info("init(): Authentication files are kept in \"" + dAuth.getAbsolutePath() + "\"");
        }

        fPasswd = new File(dAuth, cfg.get(CFG_PASSWD, DEF_PASSWD));
        if (log.isDebugEnabled()) {
            log.debug("init(): Accounts are kept in \"" + fPasswd.getAbsolutePath() + "\"");
        }
        this.fShadow = new File(dAuth, cfg.get(CFG_SHADOW, DEF_SHADOW));
        if (log.isDebugEnabled()) {
            log.debug("init(): Password hashes are kept in \"" + fShadow.getAbsolutePath() + "\"");
        }
        this.fGroup = new File(dAuth, cfg.get(CFG_GROUP, DEF_GROUP));
        if (log.isDebugEnabled()) {
            log.debug("init(): Group memberships are kept in \"" + fGroup.getAbsolutePath() + "\"");
        }
        try {
            if (!checkOrCreateDir(dAuth) ||
                !checkOrCreateFile(fPasswd) || !checkOrCreateFile(fShadow) || !checkOrCreateFile(fGroup))
            {
                log.error("init(): Initialization failed");
            }
            this.passwd = new PasswordFile(fPasswd);
            this.shadow = new ShadowFile(fShadow);
            this.group = new GroupFile(fGroup);
        }
        catch (IOException e) {
            log.error("init(): Exception while Initializing files", e);
        }
        log.debug("init(): Done");
    }

    private boolean checkOrCreateDir(File dir)
        throws IOException
    {
        boolean result = false;

        if (!dir.exists()) {
            if (dir.mkdirs()) {
                result = true;
                if (log.isInfoEnabled()) {
                    log.info("checkOrCreateDir(): Created \"" + dir.getAbsolutePath() + "\"");
                }
            }
            else {
                log.error("checkOrCreateDir(): Could not create \"" + dir.getAbsolutePath() + "\"");
            }
        }
        else if (dir.isDirectory()) {
            result = true;
            if (log.isDebugEnabled()) {
                log.debug("checkOrCreateDir(): Using existing \"" + dir.getAbsolutePath() + "\"");
            }
        }
        else {
            log.error("checkOrCreateDir(): \"" + dir.getAbsolutePath() + "\" exists, but is not a directory");
        }
        return result;
    }

    private boolean checkOrCreateFile(File file)
        throws IOException
    {
        boolean result = false;

        if (!file.exists()) {
            if (file.createNewFile()) {
                result = true;
                if (log.isInfoEnabled()) {
                    log.info("checkOrCreateFile(): Created \"" + file.getAbsolutePath() + "\"");
                }
            }
            else {
                log.error("checkOrCreateFile(): Could not create \"" + file.getAbsolutePath() + "\"");
            }
        }
        else if (file.isFile()) {
            result = true;
            if (log.isDebugEnabled()) {
                log.debug("checkOrCreateFile(): Using existing \"" + file.getAbsolutePath() + "\"");
            }
        }
        else {
            log.error("checkOrCreateFile(): \"" + file.getAbsolutePath() + "\" exists, but is not a file");
        }
        return result;
    }

    public PasswordFile getPasswd() {
        return this.passwd;
    }
    public ShadowFile getShadow() {
        return this.shadow;
    }

    public GroupFile getGroup() {
        return this.group;
    }
}
