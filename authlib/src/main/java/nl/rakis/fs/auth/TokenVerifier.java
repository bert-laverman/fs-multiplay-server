package nl.rakis.fs.auth;

import nl.rakis.fs.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.io.pem.PemReader;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * A TokenVerifier can be used to decode JWT tokens.
 */
@RequestScoped
public class TokenVerifier
{

    private static final Logger log = LogManager.getLogger(TokenVerifier.class);

    public static final String URL_PUBLICKEY = "nl.rakis.fs.url.publickey";

    private RSAPublicKey pubKey;

    @Inject
    private Config config;

    private void setKey(String key) {
        pubKey = null;
        try {
            log.debug("setKey(): Loading key data and building spec");
            try (StringReader sr = new StringReader(key + "\n\n")) {
                PemReader pr = new PemReader(sr);
                X509EncodedKeySpec spec = new X509EncodedKeySpec(pr.readPemObject().getContent());
                KeyFactory kf = KeyFactory.getInstance("RSA");
                log.debug("setKey(): Generating RSA key");
                PublicKey pubKeyTmp = kf.generatePublic(spec);
                if (pubKeyTmp instanceof RSAPublicKey) {
                    pubKey = (RSAPublicKey) pubKeyTmp;
                } else {
                    log.error("setKey(): Stored key was not an RSA Public Key");
                }
            }
        }
        catch (IOException e) {
            log.error("setKey(): Failed to read Public Key", e);
        }
        catch (NoSuchAlgorithmException e) {
            log.error("setKey(): RSA algorithm not available", e);
        }
        catch (InvalidKeySpecException e) {
            log.error("setKey(): Public key corrupted", e);
        }
    }

    @PostConstruct
    private void init()
    {
        log.debug("init()");

        final String keyUrl = config.get(URL_PUBLICKEY);
        if (keyUrl != null) {
            try {
                Client keyClient = ClientBuilder.newClient();
                setKey(keyClient.target(keyUrl).request().get(String.class));
            }
            catch (ProcessingException e) {
                log.fatal("init(): Failed to process Public Key from \"" + keyUrl + "\"", e);
            }
            catch (WebApplicationException e) {
                log.fatal("init(): Failed to obtain Public Key from \"" + keyUrl + "\"", e);
            }
        }
        else {
            log.fatal("init(): Don't know where to get the Public Key");
        }
        log.debug("init(): Done");
    }

    public Token decodeToken(String authHeader)
            throws NotAuthorizedException
    {
        if (pubKey == null) {
            log.fatal("decodeToken(): No public key");
            throw new NotAuthorizedException("Failed to establish your rights.");
        }
        return Token.decode(authHeader, pubKey);
    }

}
