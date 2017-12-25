/*
 * Copyright 2016, 2017 Bert Laverman
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
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.Base64;
import java.util.UUID;

/**
 * The REST API for Authentication
 */
@Path("auth")
@RequestScoped
public class Authenticate {

    private static final Logger log = LogManager.getLogger(Authenticate.class);

    public static final String CFG_AUTH_SCOPE = "nl.rakis.fs.auth.scope";
    public static final String DEF_AUTH_SCOPE = "noreply.com";

    public static final String URL_USER_CALLSIGN = "nl.rakis.fs.url.user.callsign";
    public static final String URL_USER_SESSION = "nl.rakis.fs.url.user.session";

    public static final String NO_SESSION = "noSession";
    public static final String NO_CALLSIGN = "PH-AAA";


    public static class TokenResult {
        public String access_token;
        public String scope;
        public String token_type;
    }

    @Inject
    private Config config;

    private String authScope;

    @Inject
    private AuthFileManager files;

    @Inject
    private TokenManager tokenMgr;

    private static void delayResponse()
    {
        try {
            Thread.sleep(1000 + Math.round(Math.random()*1000));
        }
        catch (InterruptedException e) {
            log.info("Interrupted while delaying a response to a bad request");
        }
    }

    @PostConstruct
    private void init()
    {
        authScope = config.get(CFG_AUTH_SCOPE, DEF_AUTH_SCOPE);
    }

    /*
     * OAuth 2.0 Token Request with Client Credentials
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public TokenResult checkMe(@FormParam("grant_type") String grantType,
                               @FormParam("scope") String scope,
                               @HeaderParam("Authorization") String auth)
    {
        if ((grantType == null) || !grantType.equalsIgnoreCase("client_credentials")
         || (scope == null) || !scope.equalsIgnoreCase(authScope))
        {
            log.error("checkMe(): Bad request. grant_type=" + grantType + ", scope=" + scope);
            delayResponse();
            throw new NotAuthorizedException("Bad token request");
        }
        BasicAuth ba = BasicAuthUtil.decodeAuthorizationHeader(auth);
        log.info("checkMe(): Token request for user \"" + ba.username + "\"");

        String hash = files.getShadow().getPasswordHash(ba.username);
        if ((hash == null) || hash.isEmpty()) {
            log.error("checkMe(): No hash found for user \"" + ba.username + "\"");
            delayResponse();
            throw new NotAuthorizedException("User unknown or bad password");
        }

        TokenResult result = new TokenResult();
        try {
            if (PasswordStorage.verifyPassword(ba.password, hash)) {
                UUID sessionId = UUID.randomUUID();
                Token token = tokenMgr.newToken(ba.username, sessionId.toString(), NO_SESSION, NO_CALLSIGN);
                result.token_type = Token.BEARER;
                result.scope = scope;
                result.access_token = tokenMgr.encodeToken(token);
            }
        }
        catch (PasswordStorage.CannotPerformOperationException e) {
            log.error("checkMe(): Exception verifying password", e);
            throw new ServerErrorException(500);
        }
        catch (PasswordStorage.InvalidHashException e) {
            
        }
        return result;
    }

    private boolean isCallsignOfUser(String username, String callsign) {
        boolean result = false;

        final String callsignUrl = config.get(URL_USER_CALLSIGN);
        if (callsignUrl != null) {
            Client client = null;

            try {
                client = ClientBuilder.newClient();
                WebTarget target = client.target(callsignUrl + "/" + username + "/" + callsign);
                JsonObject resp = target.request(MediaType.APPLICATION_JSON).get(JsonObject.class);
                if (!resp.isNull("callsign") && resp.getString("callsign").equalsIgnoreCase(callsign)) {
                    result = true;
                }
            }
            finally {
                if (client != null) {
                    client.close();
                }
            }
        }
        else {
            log.fatal("isCallsignOfUser(): No URL configured (" + URL_USER_CALLSIGN + ")");
        }
        return result;
    }

    private boolean isUserMemberOfSession(String username, String session) {
        boolean result = false;

        final String sessionUrl = config.get(URL_USER_SESSION);
        if (sessionUrl != null) {
            Client client = null;

            try {
                client = ClientBuilder.newClient();
                WebTarget target = client.target(sessionUrl + "/" + username + "/" + session);
                JsonObject resp = target.request(MediaType.APPLICATION_JSON).get(JsonObject.class);
                if (!resp.isNull("callsign") && resp.getString("callsign").equalsIgnoreCase(session)) {
                    result = true;
                }
            }
            finally {
                if (client != null) {
                    client.close();
                }
            }
        }
        else {
            log.fatal("isCallsignOfUser(): No URL configured (" + URL_USER_SESSION + ")");
        }
        return result;
    }

    @PUT
    @Path("/token")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TokenResult updateToken(AuthenticatedUser user, @HeaderParam("Authorization") String auth)
    {
        Token token = tokenMgr.decodeToken(auth);

        if ((user.getUsername() != null) && !user.getUsername().equalsIgnoreCase(token.getUsername())) {
            throw new NotAuthorizedException("Cannot change username on open session");
        }
        if ((user.getSessionId() != null) && !user.getSessionId().equalsIgnoreCase(token.getSessionId())) {
            throw new NotAuthorizedException("Cannot change sessionId on open session");
        }

        if ((user.getSession() == null) || user.getSession().isEmpty()) {
            user.setSession(token.getSession());
        }
        else if (!isUserMemberOfSession(token.getUsername(), user.getSession())) {
            log.error("updateToken(): Tried to set session to \"" + user.getSession() + "\", but could not verify membership");
            throw new NotAuthorizedException("Not your session");
        }

        if ((user.getCallsign() == null) || user.getCallsign().isEmpty()) {
            user.setCallsign(token.getCallsign());
        }
        else if (!isCallsignOfUser(token.getUsername(), user.getCallsign())) {
            log.error("updateToken(): Tried to set callsign to \"" + user.getCallsign() + "\", but could not verify ownership");
            throw new NotAuthorizedException("Not your callsign");
        }

        TokenResult result = new TokenResult();
        Token newToken = tokenMgr.newToken(token.getUsername(), token.getSessionId(), user.getSession(), user.getCallsign());
        result.token_type = Token.BEARER;
        result.scope = authScope;
        result.access_token = tokenMgr.encodeToken(newToken);

        return result;
    }

    @GET
    @Path("/publickey")
    @Produces(MediaType.TEXT_PLAIN)
    public String getPublicKey() {
        StringBuilder bld = new StringBuilder();

        bld.append("-----BEGIN RSA PUBLIC KEY-----\n");
        bld.append(Base64.getMimeEncoder().encodeToString(tokenMgr.getPublicKey().getEncoded()));
        bld.append("-----END RSA PUBLIC KEY-----\n");

        return bld.toString();
    }
}
