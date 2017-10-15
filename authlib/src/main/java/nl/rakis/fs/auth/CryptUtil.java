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

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ws.rs.NotAuthorizedException;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

public class CryptUtil
{

    /**
     * Encrypt the given string and encode with Base64 encoding, fit for usage in URLs.
     * 
     * @param s The string to encode
     * @param key The key to use for encoding
     * @return The encoded string
     * @exception RuntimeException If something is wrong with the encryption runtime.
     * @exception NotAuthorizedException If an exception is thrown due to bad input or a bad key
     */
    public static String encrypt(String s, Key key)
        throws NotAuthorizedException
    {
        String result = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(ENCRYPT_MODE, key);

            result = Base64.getUrlEncoder().encodeToString(cipher.doFinal(s.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new NotAuthorizedException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return result;

    }

    /**
     * Decrypt the given string, which is supposed to be Base64 encoded, fit for usage in URLs.
     * 
     * @param s The string to decode
     * @param key The key to use for encoding
     * @return The decoded string
     * @exception RuntimeException If something is wrong with the encryption runtime.
     * @exception NotAuthorizedException If an exception is thrown due to bad input or a bad key
     */
    public static String decrypt(String s, Key key)
        throws NotAuthorizedException
    {
        String result = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(DECRYPT_MODE, key);

            result = new String(cipher.doFinal(Base64.getUrlDecoder().decode(s)), "UTF-8");;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new NotAuthorizedException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return result;

    }
}