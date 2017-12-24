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

import java.io.File;

import nl.rakis.fs.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

@RunWith(CdiRunner.class)
@ActivatedAlternatives(TestConfig.class)
public class TestTokenManager
{
    
    private static final Logger log = LogManager.getLogger(TestTokenManager.class);


    @Inject
    private Config cfg;
    @Inject
    private TokenManager mgr;

    private void buildTokenManager() {
        final File storeDir = new File("build");

        File keyPath = new File(storeDir, "public.pem");
        if (keyPath.exists()) {
            log.debug("buildTokenManager(): Cleaning up old \"" + keyPath + "\"");
            keyPath.delete();
        }
        keyPath = new File(storeDir, "private.pem");
        if (keyPath.exists()) {
            log.debug("buildTokenManager(): Cleaning up old \"" + keyPath + "\"");
            keyPath.delete();
        }
    }

    @Test
    public void testKeyGeneration() {
        log.info("testKeyGeneration(): ### Start test");

        Assert.assertNotNull("Creating TokenManager should not fail", mgr);
        Assert.assertNotNull("TokenManager should auto-generate private key", mgr.getPrivateKey());
        Assert.assertNotNull("TokenManager should auto-generate public key", mgr.getPublicKey());
        
        log.info("testKeyGeneration(): ### Finished test");
    }

    @Test
    public void testNewToken() {
        log.info("testNewToken(): ### Start test");

        Token token = mgr.newToken("username", "1234-1234-1234-1234-1234-1234", "session", "PH-AAA");
        Assert.assertNotNull(token);
        Assert.assertTrue(token.isValid());
        Assert.assertEquals("username", token.getUsername());
        Assert.assertEquals("1234-1234-1234-1234-1234-1234", token.getSessionId());
        Assert.assertEquals("session", token.getSession());
        Assert.assertEquals("PH-AAA", token.getCallsign());
        final String authHeader = mgr.encodeToken(token);
        log.debug("testNewToken(): Authorization: " + authHeader);
        Assert.assertNotNull(authHeader);
        
        log.info("testNewToken(): ### Finished test");
    }
}