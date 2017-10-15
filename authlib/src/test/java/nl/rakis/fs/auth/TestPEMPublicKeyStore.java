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
import java.io.IOException;
import java.security.PublicKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


public class TestPEMPublicKeyStore
{

    private static final Logger log = LogManager.getLogger(TestTokenManager.class);
    
    @Test
    public void testStoreKey() {
        log.info("testStoreKey(): ### Start test");

        File storeDir = new File("build");
        File storePath = new File(storeDir, "test.pem");

        TokenManager mgr = new TokenManager(storeDir);
        Assert.assertNotNull("Creating TokenManager should not fail", mgr);
        Assert.assertNotNull("TokenManager should auto-generate public key", mgr.getPublicKey());

        PEMPublicKeyStore store = new PEMPublicKeyStore(storePath);
        store.storeKey(mgr.getPublicKey());

        log.info("testStoreKey(): ### Finished test");
    }

    @Test
    public void testLoadKey() {
        log.info("testLoadKey(): ### Start test");
        
        File storePath = new File("test.pem");

        try {
            FileUtil.createFile(storePath, "-----BEGIN RSA PUBLIC KEY -----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkCxYlEhPS8WK4NNLBhV6BA4MOKNJaAHw\n" +
            "PVp6HKesG7zKJqKDQD3kzm4EX/Whbyx+qOK4rWe8sm/Ut0bORSO2sdwTlgpuvvV1IWBVW1/UXkH2\n" +
            "UZlal0waa09vn24kKt+1JN9aqO2jfyuGThP/Ig+J7WCxMatzqyJwSbdfoh05EHO5RIjyKLU/pvGQ\n" +
            "GwO1/SN6ps2Dtq4XwqRJKDOfkMKqT8T7Ui8amLP2w0K7IOxP4ojqV/mDcG0trBpYDPw3pYmFyHZz\n" +
            "b64ojbfDBhOOP0h7JGoOuY0LCEaGxnPUO0KHFe2Fc4wfkDixmEIzF09o5OjJxoasoW0+eY4+nNfm\n" +
            "UKWWAwIDAQAB\n" +
            "-----END RSA PUBLIC KEY -----");
        }
        catch (IOException e) {
            Assert.fail(e.getMessage());
        }

        PEMPublicKeyStore store = new PEMPublicKeyStore(storePath);
        PublicKey key = store.loadKey();
        Assert.assertNotNull("PEMPublicKeyStore.loadKey should return a value", key);
        final String clazzName = key.getClass().getSimpleName();
        log.debug("testLoadKey(): clazzName = \"" + clazzName + "\"");
        Assert.assertTrue("Key should be an RSA public key", clazzName.startsWith("RSAPublicKey"));

        log.info("testLoadKey(): ### Finished test");
    }
}