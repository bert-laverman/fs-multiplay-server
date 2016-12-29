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
package nl.rakis.fs.multiplayserver.api;

import com.auth0.jwt.interfaces.DecodedJWT;
import nl.rakis.fs.UserInfo;
import nl.rakis.fs.NamedObject;
import nl.rakis.fs.SessionInfo;
import nl.rakis.fs.db.Sessions;
import nl.rakis.fs.security.EncryptDecrypt;

import javax.cache.annotation.CacheResult;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.UUID;

@Stateless
@Path("session")
public class Session {

    @Inject
    private Sessions sessions;

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

        return sessions.getAllSessions();
    }

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public SessionInfo get(@NotNull @PathParam("name") String name,
                           @HeaderParam("authorization") String authHeader)
    {
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);

        if (!EncryptDecrypt.getUsername(token).equalsIgnoreCase(UserInfo.ADMIN_USER) &&
                !EncryptDecrypt.getSession(token).equals(name))
        {
            throw new NotAuthorizedException(("Not your session"));
        }

        return findSession(name);
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

        if (!EncryptDecrypt.getUsername(token).equalsIgnoreCase(UserInfo.ADMIN_USER)) {
            throw new NotAuthorizedException("Only admin users can change session details");
        }
        SessionInfo result = findSession(name);

        if (result == null) {
            throw new NotFoundException("No such session");
        }

        result.setDescription(session.getDescription());
        if (!result.getName().equalsIgnoreCase(session.getName())) {
            // Be careful changing the name
            if (findSession(session.getName()) != null) {
                throw new ForbiddenException("Session with that name already exists");
            }
            result.setName(session.getName());
        }
        sessions.setSession(result);

        return result;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public SessionInfo newSession(NamedObject session,
                                  @HeaderParam("authorization") String authHeader)
    {
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);

        if (!EncryptDecrypt.getUsername(token).equalsIgnoreCase(UserInfo.ADMIN_USER)) {
            throw new NotAuthorizedException("Only admin users can create sessions");
        }

        if (findSession(session.getName()) != null) {
            throw new ForbiddenException("Session with that name already exists");
        }

        SessionInfo result = new SessionInfo(session.getName(), session.getDescription());

        sessions.setSession(result);

        return result;
    }

    @DELETE
    @Path("{name}")
    public void removeSession(@NotNull @PathParam("name") String name,
                              @HeaderParam("authorization") String authHeader)
    {
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);

        if (!EncryptDecrypt.getUsername(token).equalsIgnoreCase(UserInfo.ADMIN_USER)) {
            throw new NotAuthorizedException("Only admin users can create sessions");
        }

        if (EncryptDecrypt.getSession(token).equals(name)) {
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
        SessionInfo session = findSession(authInfo.getSession());
        if (session == null) {
            throw new NotFoundException("Session not found");
        }
        authInfo.setSession(session.getName());
        return EncryptDecrypt.newToken(authInfo);
    }

    @GET
    @Path("logout")
    @Produces(MediaType.TEXT_PLAIN)
    public String logout(@HeaderParam("authorization")String authHeader) {
        String result = "Not logged in";
        if (authHeader != null) {
            DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
            result = EncryptDecrypt.getUsername(token);
        }
        return result;
    }
}