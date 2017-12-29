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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class PEMPrivateKeyStore
    extends PEMKeyStore
{
 
    private static final Logger log = LogManager.getLogger(PEMPrivateKeyStore.class);

    public PEMPrivateKeyStore(File path) {
        super(path);
    }

    public PrivateKey loadKey() {
        PrivateKey result = null;

        try {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(readData());
            KeyFactory kf = KeyFactory.getInstance("RSA");
            result = kf.generatePrivate(spec);
        }
        catch (FileNotFoundException e) {
            // This isn't a big thing that requires a stack trace
            log.error("loadPrivateKey(): No file found named \"" + getPath() + "\"");
        }
        catch (IOException e) {
            log.error("loadPrivateKey(): Error reading file", e);
        }
        catch (NoSuchAlgorithmException e) {
            log.error("loadPrivateKey(): RSA algorithm not available", e);
        }
        catch (InvalidKeySpecException e) {
            log.error("loadPrivateKey(): Private key corrupted");
        }
        return result;
    }

    public void storeKey(PrivateKey key) {
        storeKey(key.getEncoded(), "RSA PRIVATE KEY");
    }
}