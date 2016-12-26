/*
 * Copyright 2016 Bert Laverman
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
package nl.rakis.fs.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import nl.rakis.fs.UserInfo;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.ws.rs.NotAuthorizedException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

/**
 * Created by bertl on 12/19/2016.
 */
public class EncryptDecrypt {

    public static final String BEARER_PREFIX = "BEARER ";
    public static final String ISSUER = "FSMultiPlayer";
    public static final String CLAIM_USERNAME = "username";
    public static final String CLAIM_SESSION = "session";

    private static KeyPair generateKeyPair() {
        KeyPair result = null;
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            result = generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private final static class StaticHolder {
        public final static KeyPair pair = generateKeyPair();
    }

    public static RSAPublicKey getPublicKey() {
        final PublicKey result = StaticHolder.pair.getPublic();
        return (result instanceof RSAPublicKey) ? ((RSAPublicKey)result) : null;
    }
    public static RSAPrivateKey getPrivateKey() {
        final PrivateKey result = StaticHolder.pair.getPrivate();
        return (result instanceof RSAPrivateKey) ? ((RSAPrivateKey)result) : null;
    }

    private static String process(String s, int mode, Key key) {
        String result = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(mode, key);
            if (mode == ENCRYPT_MODE) {
                result = Base64.getEncoder().encodeToString(cipher.doFinal(s.getBytes("UTF-8")));
            }
            else {
                result = new String(cipher.doFinal(Base64.getDecoder().decode(s)), "UTF-8");
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    public static String encryptWithPrivate(String s) {
        return process(s, ENCRYPT_MODE, getPrivateKey());
    }

    public static String encryptWithPublic(String s) {
        return process(s, ENCRYPT_MODE, getPublicKey());
    }

    public static String decryptWithPrivate(String s) {
        return process(s, DECRYPT_MODE, getPrivateKey());
    }

    public static String decryptWithPublic(String s) {
        return process(s, DECRYPT_MODE, getPublicKey());
    }

    public static String newToken(UserInfo authInfo) {
        return JWT.create()
                .withIssuer(ISSUER)
                .withClaim(CLAIM_USERNAME, authInfo.getUsername())
                .withClaim(CLAIM_SESSION, authInfo.getSession())
                .sign(Algorithm.RSA512(getPrivateKey()));
    }

    public static JWTCreator.Builder newToken(DecodedJWT token) {
        return JWT.create()
                .withIssuer(ISSUER)
                .withClaim(CLAIM_USERNAME, token.getClaim(CLAIM_USERNAME).asString())
                .withClaim(CLAIM_SESSION, token.getClaim(CLAIM_SESSION).asString());
    }

    public static DecodedJWT decodeToken(String authHeader) {
        System.err.println("decodeToken()");
        final int prefixLen = BEARER_PREFIX.length();
        final String prefix = ((authHeader != null) && (authHeader.length() > prefixLen)) ? authHeader.substring(0, prefixLen) : "";
        if (!prefix.equalsIgnoreCase(BEARER_PREFIX)) {
            throw new NotAuthorizedException("Bad token");
        }
        final String tokenString = authHeader.substring(prefixLen);
        JWT result = null;
        try {
            result = JWT.decode(tokenString);
        } catch (JWTDecodeException e) {
            throw new NotAuthorizedException("Bad token");
        }
        return result;
    }

    public static String getUsername(DecodedJWT token) {
        return token.getClaim(CLAIM_USERNAME).asString();
    }

    public static String getSession(DecodedJWT token) {
        return token.getClaim(CLAIM_SESSION).asString();
    }

    public static void verifyToken(DecodedJWT token) throws NotAuthorizedException {
        System.err.println("verifyToken()");
        try {
            JWTVerifier verifier = JWT.require(Algorithm.RSA512(EncryptDecrypt.getPublicKey()))
                    .withIssuer(ISSUER)
                    .build();
            DecodedJWT verifyResult = verifier.verify(token.getToken());
        } catch (JWTDecodeException e) {
            e.printStackTrace();
            throw new NotAuthorizedException("Bad token");
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            throw new NotAuthorizedException("Bad token");
        }
    }

}
