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
package nl.rakis.fs.api;

import nl.rakis.fs.api.rules.SessionRules;
import nl.rakis.fs.auth.Token;
import nl.rakis.fs.auth.TokenVerifier;
import nl.rakis.fs.info.JsonFields;
import nl.rakis.fs.info.SessionInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("session")
@RequestScoped
public class SessionAPI {

    private static final Logger log = LogManager.getLogger(SessionAPI.class);

    @Inject
    private TokenVerifier verifier;

    private SessionRules rules;

    @PostConstruct
    private void init() {
        rules = new SessionRules();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SessionInfo> getSessionList(@HeaderParam("authorization") String authHdr)
    {
        List<SessionInfo> result = new ArrayList<>();

        result.add(getSession("Sundowners", authHdr));

        return result;
    }

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public SessionInfo getSession(@PathParam("name")            String name,
                                  @HeaderParam("authorization") String authHdr)
    {
        Token token = verifier.decodeToken(authHdr);

        SessionInfo result = new SessionInfo(name, "FSGG Sundowners");

        rules.cleanRecord(result, token);

        return result;
    }

    @PUT
    @Path("{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SessionInfo updateSession(@PathParam("name")            String name,
                                                                   JsonObject session,
                                     @HeaderParam("authorization") String authHdr)
    {
        Token token = verifier.decodeToken(authHdr);

        SessionInfo result = getSession(name, authHdr);

        rules.checkUpdate(result, session, token);
        result.updateFromJsonObject(session);

        return result;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String createSession(                              JsonObject session,
                                @HeaderParam("authorization") String authHdr)
    {
        Token token = verifier.decodeToken(authHdr);

        rules.checkCreate(session, token);

        return session.getString(JsonFields.FIELD_NAME);
    }
    
}
