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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PEMPublicKeyStore
    extends PEMKeyStore
{
 
    private static final Logger log = LogManager.getLogger(PEMPrivateKeyStore.class);

    public PEMPublicKeyStore(File path) {
        super(path);
    }

    public PublicKey loadKey() {
        PublicKey result = null;

        try {
            log.debug("loadPublicKey(): Loading key data and building spec");
            X509EncodedKeySpec spec = new X509EncodedKeySpec(readData());
            KeyFactory kf = KeyFactory.getInstance("RSA");
            log.debug("loadPublicKey(): Generating RSA key");
            result = kf.generatePublic(spec);
            if (log.isDebugEnabled()) {
                log.debug("loadPublicKey(): Result is a " + result.getClass().getName());
            }
        }
        catch (FileNotFoundException e) {
            // This isn't a big thing that requires a stack trace
            log.error("loadPrivateKey(): No file found named \"" + getPath() + "\"");
        }
        catch (IOException e) {
            log.error("loadPublicKey(): Error reading file", e);
        }
        catch (NoSuchAlgorithmException e) {
            log.error("loadPublicKey(): RSA algorithm not available", e);
        }
        catch (InvalidKeySpecException e) {
            log.error("loadPublicKey(): Public key corrupted", e);
        }
        return result;
    }

    public void storeKey(PublicKey key) {
        storeKey("RSA PUBLIC KEY", key.getEncoded());
    }

}