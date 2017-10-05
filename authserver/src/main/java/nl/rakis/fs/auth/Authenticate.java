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
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * The REST API for Authentication
 */
@Path("/token")
@RequestScoped
public class Authenticate {

    public static class TokenResult {
        public String access_token;
        public String scope;
        public String token_type;
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public TokenResult checkMe(@FormParam("grant_type") String grantType,
                               @FormParam("scope") String scope,
                               @HeaderParam("Authorization") String auth)
    {
        TokenResult result = new TokenResult();

        BasicAuth ba = BasicAuthUtil.decodeAuthorizationHeader(auth);

        return result;
    }
}
