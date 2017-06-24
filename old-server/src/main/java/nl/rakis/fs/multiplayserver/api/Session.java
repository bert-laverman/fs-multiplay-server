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
package nl.rakis.fs.multiplayserver.api;

import com.auth0.jwt.interfaces.DecodedJWT;
import nl.rakis.fs.*;
import nl.rakis.fs.db.*;
import nl.rakis.fs.multiplayserver.ClientSessionHandler;
import nl.rakis.fs.security.EncryptDecrypt;

import javax.cache.annotation.CacheResult;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Collection;
import java.util.logging.Logger;

@ApplicationScoped
@Path("session")
public class Session {

    private static final Logger log = Logger.getLogger(Session.class.getName());

    @Inject
    private Sessions sessions;
    @Inject
    private UserSessions userSessions;
    @Inject
    private nl.rakis.fs.db.Aircraft aircraft;
    @Inject
    private Extras extras;
    @Inject
    private ClientSessionHandler sessionHandler;

    @Context
    private UriInfo uriInfo;

    @CacheResult
    private SessionInfo findSession(String name)
    {
        return sessions.getSession(name);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<SessionInfo> getAll(@HeaderParam("authorization")String authHeader)
    {
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);
        userSessions.get(EncryptDecrypt.getSessionId(token));

        return sessions.getAllSessions();
    }

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public SessionInfo get(@NotNull @PathParam("name") String name,
                           @QueryParam("_expand") String expand,
                           @HeaderParam("authorization") String authHeader)
    {
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);
        UserSessionInfo userSession = userSessions.get(EncryptDecrypt.getSessionId(token));

        if (!userSession.getUsername().equalsIgnoreCase(UserInfo.ADMIN_USER) &&
                !userSession.getSession().equals(name))
        {
            throw new NotAuthorizedException(("Not your session"));
        }

        SessionInfo result = findSession(name);

        if ((expand != null) && !expand.isEmpty()) {
            log.finest("get(): _expand=\"" + expand + "\"");
            String[] fields=expand.split(",");
            for (String field: fields) {
                log.finest("get(): field=\"" + field + "\"");
                switch (field) {
                    case "aircraft":
                        result.setAircraft(aircraft.listAllAircraftInSession(name));
                        break;
                }
            }
        }

        return result;
    }

    @PUT
    @Path("{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SessionInfo put(@NotNull @PathParam("name") String name,
                           SessionInfo session,
                           @HeaderParam("authorization") String authHeader)
    {
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);
        UserSessionInfo userSession = userSessions.get(EncryptDecrypt.getSessionId(token));

        if (!userSession.getUsername().equalsIgnoreCase(UserInfo.ADMIN_USER)) {
            throw new NotAuthorizedException("Only admin users can create or update sessions");
        }
        SessionInfo result = findSession(name);
        if (result == null) { // Create
            result = session;
        }
        else { // Update
            result.setDescription(session.getDescription());
            if (!result.getName().equalsIgnoreCase(session.getName())) {
                throw new ForbiddenException("Create a new session if you want a different name");
            }
        }
        final URI uri = uriInfo.getAbsolutePath();
        result.setHref(uri.toString());
        sessions.setSession(result);

        return result;
    }

    @POST
    public Response newSession(SessionInfo session,
                               @HeaderParam("authorization") String authHeader)
    {
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);
        UserSessionInfo userSession = userSessions.get(EncryptDecrypt.getSessionId(token));

        if (!userSession.getUsername().equalsIgnoreCase(UserInfo.ADMIN_USER)) {
            throw new NotAuthorizedException("Only admin users can create sessions");
        }

        if (findSession(session.getName()) != null) {
            throw new ForbiddenException("Session with that name already exists");
        }

        SessionInfo result = new SessionInfo(session.getName(), session.getDescription());
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        uriBuilder.path(session.getName());
        final URI uri = uriBuilder.build();
        result.setHref(uri.toString());

        sessions.setSession(result);

        return Response.created(uri).build();
    }

    @DELETE
    @Path("{name}")
    public void removeSession(@NotNull @PathParam("name") String name,
                              @HeaderParam("authorization") String authHeader)
    {
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);
        UserSessionInfo userSession = userSessions.get(EncryptDecrypt.getSessionId(token));

        if (!userSession.getUsername().equalsIgnoreCase(UserInfo.ADMIN_USER)) {
            throw new NotAuthorizedException("Only admin users can create sessions");
        }

        if (userSession.getSession().equals(name)) {
            throw new ForbiddenException("Cannot remove the session you're in");
        }
        if (findSession(name) == null) {
            throw new NotFoundException("No such session found");
        }
        sessions.delete(name);
    }

    @PUT
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String login(UserInfo authInfo)
    {
        if (authInfo.getUsername() == null) {
            throw new BadRequestException("No username");
        }
        if (authInfo.getPassword() == null) {
            throw new BadRequestException("No password");
        }
        if (authInfo.getSession() == null) {
            throw new BadRequestException("No session");
        }
        if (authInfo.getCallsign() == null) {
            throw new BadRequestException("No callsign");
        }
        SessionInfo session = findSession(authInfo.getSession());
        if (session == null) {
            throw new NotFoundException("Session not found");
        }
        authInfo.setSession(session.getName());
        UserSessionInfo sessionInfo = new UserSessionInfo(authInfo.getUsername(), authInfo.getSession(), authInfo.getCallsign());

        userSessions.put(sessionInfo);

        return EncryptDecrypt.newToken(sessionInfo);
    }

    @GET
    @Path("logout")
    @Produces(MediaType.TEXT_PLAIN)
    public String logout(@HeaderParam("authorization")String authHeader) {
        String result = "Not logged in";
        if (authHeader != null) {
            DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);

            result = EncryptDecrypt.getUsername(token);
            final String sessionId = EncryptDecrypt.getSessionId(token);
            final String callsign = EncryptDecrypt.getCallsign(token);
            final String flySession = EncryptDecrypt.getSession(token);

            extras.remove(flySession, callsign, LocationInfo.class);
            extras.remove(flySession, callsign, LightInfo.class);
            extras.remove(flySession, callsign, EngineInfo.class);
            extras.remove(flySession, callsign, ControlsInfo.class);

            aircraft.removeAircraftFromSession(flySession, callsign);

            sessionHandler.removeClient(userSessions.get(sessionId));

            userSessions.remove(sessionId);
        }
        return result;
    }
}
