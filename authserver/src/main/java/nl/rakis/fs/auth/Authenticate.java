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

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The REST API for Authentication
 */
@Path("/token")
@RequestScoped
public class Authenticate {

    private static final Logger log = LogManager.getLogger(Authenticate.class);

    public static class TokenResult {
        public String access_token;
        public String scope;
        public String token_type;
    }


    private AuthFileManager files;

    private static void delayResponse()
    {
        try {
            Thread.sleep(1000 + Math.round(Math.random()*1000));
        }
        catch (InterruptedException e) {
            log.info("Interrupted while delaying a response to a bad request");
        }
    }

    /*
     * OAuth 2.0 Token Request with Client Credentials
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public TokenResult checkMe(@FormParam("grant_type") String grantType,
                               @FormParam("scope") String scope,
                               @HeaderParam("Authorization") String auth)
    {
        if ((grantType == null) || !grantType.equalsIgnoreCase("client_credentials")
         || (scope == null) || !scope.equalsIgnoreCase("fs.rakis.nl"))
        {
            log.error("checkMe(): Bad request. grant_type=" + grantType + ", scope=" + scope);
            delayResponse();
            throw new NotAuthorizedException("Bad token request");
        }
        BasicAuth ba = BasicAuthUtil.decodeAuthorizationHeader(auth);
        log.info("checkMe(): Token request for user \"" + ba.username + "\"");

        String hash = shadow.getPasswordHash(ba.username);
        if ((hash == null) || hash.isEmpty()) {
            log.error("checkMe(): No hash found for user \"" + ba.username + "\"");
            delayResponse();
            throw new NotAuthorizedException("User unknown or bad password");
        }

        TokenResult result = new TokenResult();
        try {
            if (PasswordStorage.verifyPassword(ba.password, hash)) {
                result.token_type = "bearer";
                result.scope = scope;
                result.access_token = "token";
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
}
