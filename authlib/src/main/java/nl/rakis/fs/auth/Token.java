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

import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.ws.rs.NotAuthorizedException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

public class Token
{

    public static final String BEARER = "BEARER";
    private static final String BEARER_PREFIX = BEARER + " ";

    public static final String ISSUER = "FSMultiPlayer";
    public static final String CLAIM_SESSIONID = "sessionId";
    public static final String CLAIM_USERNAME = "username";
    public static final String CLAIM_SESSION = "session";
    public static final String CLAIM_CALLSIGN = "callsign";
    
    private static final Logger log = LogManager.getLogger(Token.class);

    private boolean valid = false;
    private String username = null;
    private String sessionId = null;
    private String session = null;
    private String callsign = null;

    public Token() {
        log.debug("Token()");
    }

    public Token(String username, String session, String callsign) {
        if (log.isDebugEnabled()) {
            log.debug("Token(\"" + username + "\", \"" + session + "\", \"" + callsign + "\")");
        }
        this.username = username;
        this.session = session;
        this.callsign = callsign;
    }

    public Token(String username, String sessionId, String session, String callsign) {
        if (log.isDebugEnabled()) {
            log.debug("Token(\"" + username + "\", \"" + sessionId + "\", \"" + session + "\", \"" + callsign + "\", " + valid + ")");
        }
        this.username = username;
        this.sessionId = sessionId;
        this.session = session;
        this.callsign = callsign;

        setValid((username != null) && (sessionId != null) && (session!= null) && (callsign != null));
    }

    private Token(String username, String sessionId, String session, String callsign, boolean valid) {
        if (log.isDebugEnabled()) {
            log.debug("Token(\"" + username + "\", \"" + sessionId + "\", \"" + session + "\", \"" + callsign + "\", " + valid + ")");
        }
        this.username = username;
        this.sessionId = sessionId;
        this.session = session;
        this.callsign = callsign;
        this.valid = valid;
    }

    /**
     * Return a Token from the given "Authorization" header, using the public key to verify the signature.
     * 
     * @param authHeader The value of the "Authorization" header.
     * @param key The public key to be used for verification of the signature.
     * @return The new Token object.
     * @exception NotAuthorizedException Thrown if verification fails or any claims are missing.
     */
    public static Token decode(String authHeader, RSAPublicKey key)
        throws NotAuthorizedException
    {
        final int prefixLen = BEARER_PREFIX.length();
        final String prefix = ((authHeader != null) && (authHeader.length() > prefixLen)) ? authHeader.substring(0, prefixLen) : "";
        if (!prefix.equalsIgnoreCase(BEARER_PREFIX)) {
            throw new NotAuthorizedException("Bad token");
        }
        final String tokenString = authHeader.substring(prefixLen);
        DecodedJWT decoded = null;
        try {
            JWT token = JWT.decode(tokenString);
            
            JWTVerifier verifier = JWT.require(Algorithm.RSA512(key))
            .withIssuer(ISSUER)
            .build();
            decoded = verifier.verify(token.getToken());
        } catch (JWTDecodeException e) {
            log.error("decode(): Bad token passed", e);
            throw new NotAuthorizedException("Bad token");
        } catch (JWTVerificationException e) {
            log.error("decode(): Verification exception caught");
            throw new NotAuthorizedException("Bad token");
        }

        Claim claim = decoded.getClaim(CLAIM_USERNAME);
        final String username = claim.asString();
        if (claim.isNull() || (username == null)) {
            log.error("decode(): No username in token");
            throw new NotAuthorizedException("Bad token");
        }

        claim = decoded.getClaim(CLAIM_SESSIONID);
        final String sessionId = claim.asString();
        if (claim.isNull() || (sessionId == null)) {
            log.error("decode(): No sessionId in token");
            throw new NotAuthorizedException("Bad token");
        }

        claim = decoded.getClaim(CLAIM_SESSION);
        final String session = claim.asString();
        if (claim.isNull() || (session == null)) {
            log.error("decode(): No session in token");
            throw new NotAuthorizedException("Bad token");
        }

        claim = decoded.getClaim(CLAIM_CALLSIGN);
        final String callsign = claim.asString();
        if (claim.isNull() || (callsign == null)) {
            log.error("decode(): No callsign in token");
            throw new NotAuthorizedException("Bad token");
        }

        return new Token(username, sessionId, session, callsign, true);
    }

    /**
     * Build the bearer token (with the prefix "BEARER "), ready to be set as "Authorization" header.
     * 
     * @param key The private key to use.
     * @return The encoded token.
     */
    public String encode(RSAPrivateKey key)
    {
        return BEARER_PREFIX + JWT.create()
            .withIssuer(ISSUER)
            .withClaim(CLAIM_USERNAME, username)
            .withClaim(CLAIM_SESSIONID, sessionId)
            .withClaim(CLAIM_SESSION, session)
            .withClaim(CLAIM_CALLSIGN, callsign)
            .sign(Algorithm.RSA512(key));
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;

        setValid((username != null) && (sessionId != null) && (session!= null) && (callsign != null));
    }

    public String getSessionId() {
        return sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;

        setValid((username != null) && (sessionId != null) && (session!= null) && (callsign != null));
    }

    public String getSession() {
        return session;
    }
    public void setSession(String session) {
        this.session = session;

        setValid((username != null) && (sessionId != null) && (session!= null) && (callsign != null));
    }

    public String getCallsign() {
        return callsign;
    }
    public void setCallsign(String callsign) {
        this.callsign = callsign;

        setValid((username != null) && (sessionId != null) && (session!= null) && (callsign != null));
    }

    public boolean isValid() {
        return valid;
    }
    private void setValid(boolean valid) {
        this.valid = valid;
    }
}