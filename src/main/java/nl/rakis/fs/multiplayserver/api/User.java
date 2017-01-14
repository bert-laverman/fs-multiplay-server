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
import nl.rakis.fs.AircraftStatus;
import nl.rakis.fs.UserInfo;
import nl.rakis.fs.UserInfo;
import nl.rakis.fs.db.Users;
import nl.rakis.fs.security.EncryptDecrypt;
import nl.rakis.fs.security.PasswordStorage;

import javax.cache.annotation.CacheResult;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Stateless
@Path("user")
public class User {

    @Inject
    private Users users;

    @CacheResult
    private UserInfo findUser(String username) {
        return users.getUser(username);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<UserInfo> getAll(@HeaderParam("authorization")String authHeader)
    {
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);

        if (!EncryptDecrypt.getUsername(token).equalsIgnoreCase(UserInfo.ADMIN_USER)) {
            throw new NotAuthorizedException("Only admin users can query users");
        }
        return users.getAllUsersScrubbed();
    }

    @GET
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserInfo get(@PathParam("username") String username, @HeaderParam("authorization")String authHeader)
            throws WebApplicationException
    {
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);

        UserInfo result = findUser(username);
        if (result == null) {
            throw new NotFoundException("No such user");
        }
        return Users.scrubUser(result);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserInfo put(UserInfo user, @HeaderParam("authorization")String authHeader)
            throws WebApplicationException
    {
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);

        final String thisUser = EncryptDecrypt.getUsername(token);

        if (!thisUser.equalsIgnoreCase(user.getUsername()) && !thisUser.equalsIgnoreCase(UserInfo.ADMIN_USER)) {
            throw new NotAuthorizedException("You can only update yourself");
        }
        UserInfo result = findUser(user.getUsername());
        if (result == null) {
            throw new NotFoundException("You don't exist. Go away!");
        }
        if ((user.getPassword() != null) && !user.getPassword().equals("")) {
            try {
                result.setPassword(PasswordStorage.createHash(user.getPassword()));
            } catch (PasswordStorage.CannotPerformOperationException e) {
                throw new InternalServerErrorException("Yikes, I forgot how to hash passwords!");
            }
        }
        // update default session
        result.setSession(user.getSession());

        users.setUser(result);

        Users.scrubUser(result);
        return result;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserInfo post(UserInfo user, @HeaderParam("authorization")String authHeader)
            throws WebApplicationException
    {
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);

        if (!EncryptDecrypt.getUsername(token).equals(UserInfo.ADMIN_USER)) {
            throw new NotAuthorizedException("Only admin users can create users");
        }
        if ((user.getUsername() == null) || user.getUsername().equals("")) {
            throw new BadRequestException("Users must have names");
        }
        if ((user.getPassword() == null) || user.getPassword().equals("")) {
            throw new BadRequestException("Users must have passwords");
        }
        if (users.getUser(user.getUsername()) != null) {
            throw new BadRequestException("User already exists");
        }
        try {
            user.setPassword(PasswordStorage.createHash(user.getPassword()));
        } catch (PasswordStorage.CannotPerformOperationException e) {
            throw new InternalServerErrorException("Yikes, I forgot how to hash passwords!");
        }
        users.setUser(user);

        Users.scrubUser(user);
        return user;
    }
}
