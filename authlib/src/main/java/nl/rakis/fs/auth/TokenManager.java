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
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.ws.rs.NotAuthorizedException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TokenManager
{

    private static final Logger log = LogManager.getLogger(TokenManager.class);

    public static KeyPair pair = null;

    private File keyDir;
    private File priKeyPath;
    private File pubKeyPath;
    private PEMPrivateKeyStore priStore;
    private RSAPrivateKey priKey;
    private PEMPublicKeyStore pubStore;
    private RSAPublicKey pubKey;

    public TokenManager(File certPath) {
        if (log.isDebugEnabled()) {
            log.debug("TokenManager(\"" + certPath + "\")");
        }

        this.keyDir = certPath;
        this.priKeyPath = new File(keyDir, "private.pem");
        this.pubKeyPath = new File(keyDir, "public.pem");
        this.priStore = new PEMPrivateKeyStore(priKeyPath);
        this.pubStore = new PEMPublicKeyStore(pubKeyPath);
        
        init();
        log.debug("TokenManager(): Done");
    }

    private KeyPair generateKeyPair() {
        log.debug("generateKeyPair()");
        KeyPair result = null;
        try {
            log.debug("generateKeyPair(): Getting generator for algorithm \"RSA\"");
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            log.debug("generateKeyPair(): Generating keypair");
            result = generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        log.debug("generateKeyPair(): " + ((result != null) ? "Success" : "Failed"));
        return result;
    }

    public void init() {
        log.debug("init()");

        if (keyDir == null) {
            log.error("init(): No path to store/retrieve keys set!");
        }
        else if (!keyDir.exists()) {
            log.error("init(): Configured path \"" + keyDir.getAbsolutePath() + "\" for key storage doesn't exist");
        }
        else if (!keyDir.isDirectory()) {
            log.error("init(): Configured path \"" + keyDir.getAbsolutePath() + "\" for key storage must be a directory");
        }
        else if (!loadKeys()) {
            KeyPair pair = generateKeyPair();
            priKey = (pair.getPrivate() instanceof RSAPrivateKey) ? (RSAPrivateKey)(pair.getPrivate()) : null;
            pubKey = (pair.getPublic() instanceof RSAPublicKey) ? (RSAPublicKey)(pair.getPublic()) : null;

            storeKeys();
        }

        log.debug("init(): Done");
    }

    private boolean loadKeys() {
        log.info("loadKeys()");
        boolean result = false;

        priKey = null;
        pubKey = null;

        if (log.isDebugEnabled()) {
            log.debug("loadKeys(): Attempting to load private key from \"" + priKeyPath.getAbsolutePath() + "\"");
        }
        PrivateKey priKeyTmp = priStore.loadKey();
        if (priKeyTmp instanceof RSAPrivateKey) {
            priKey = (RSAPrivateKey)priKeyTmp;
        }
        else {
            log.error("loadKeys(): Stored key was not an RSA Private Key");
        }

        if (log.isDebugEnabled()) {
            log.debug("loadKeys(): Attempting to load public key from \"" + pubKeyPath.getAbsolutePath() + "\"");
        }
        PublicKey pubKeyTmp = pubStore.loadKey();
        if (pubKeyTmp instanceof RSAPublicKey) {
            pubKey = (RSAPublicKey) pubKeyTmp;
        }
        else {
            log.error("loadKeys(): Stored key was not an RSA Public Key");
        }

        if ((priKey == null) || (pubKey == null)) {
            log.warn("loadKeys(): Could not load stored keys");
        }
        else {
            log.info("loadKeys(): Successfully restored keys");
            result = true;
        }

        return result;
    }

    private void storeKeys() {
        log.info("storeKeys()");

        priStore.storeKey(priKey);
        pubStore.storeKey(pubKey);

        log.debug("storeKeys(): Done");
    }

    public PublicKey getPublicKey() {
        return pubKey;
    }

    public PrivateKey getPrivateKey() {
        return priKey;
    }

    public Token newToken(String username, String sessionId, String session, String callsign) {
        return new Token(username, sessionId, session, callsign);
    }

    public String encodeToken(Token token) {
        return token.encode(priKey);
    }

    public Token decodeToken(String authHeader)
        throws NotAuthorizedException
    {
        return Token.decode(authHeader, pubKey);
    }
}