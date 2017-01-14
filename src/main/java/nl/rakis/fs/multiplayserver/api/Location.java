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
import nl.rakis.fs.AircraftInfo;
import nl.rakis.fs.LocationInfo;
import nl.rakis.fs.db.*;
import nl.rakis.fs.multiplayserver.ClientSessionHandler;
import nl.rakis.fs.security.EncryptDecrypt;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Aircraft location.
 */
@Stateless
@Path("location")
public class Location
{
    private static final Logger log = Logger.getLogger(Location.class.getName());

    @Inject
    private Locations locations;
    @Inject
    private nl.rakis.fs.db.Aircraft aircrafts;
    @Inject
    private ClientSessionHandler sessionHandler;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<LocationInfo> getAll(@HeaderParam("authorization")String authHeader)
    {
        log.info("getAll()");
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);

        log.info("getAll(): Done");
        return locations.getAll(EncryptDecrypt.getSession(token));
    }

    @GET
    @Path("{callsign}")
    @Produces(MediaType.APPLICATION_JSON)
    public LocationInfo get(@PathParam("callsign") String callsign, @HeaderParam("authorization")String authHeader)
    {
        log.info("get(): callsign=\"" + callsign + "\"");
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);

        LocationInfo result = locations.getLocation(callsign, EncryptDecrypt.getSession(token));
        if (result == null) {
            throw new NotFoundException("No such callsign");
        }
        log.info("get(): Done");
        return result;
    }

    @PUT
    @Path("{callsign}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<LocationInfo> put(LocationInfo location,
                                  @PathParam("callsign") String callsign,
                                  @HeaderParam("authorization")String authHeader)
    {
        log.info("put(): callsign=\"" + callsign + "\"");
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);

        final String session = EncryptDecrypt.getSession(token);
        final String username = EncryptDecrypt.getUsername(token);
        AircraftInfo aircraft = aircrafts.getAircraftInSession(callsign, session);

        if (aircraft == null) {
            throw new NotFoundException("Callsign not found");
        }
        if (!username.equalsIgnoreCase(aircraft.getUsername())) {
            throw new NotAuthorizedException("You can only send locations for your own aircraft");
        }

        location.setCallsign(callsign);
        locations.setLocation(location, callsign, session);

        sessionHandler.sendToAllConnectedSessionsButOne(location.toJsonObject(), session, callsign);

        return locations.getAll(session).stream()
                .filter((LocationInfo li) -> !li.getCallsign().equalsIgnoreCase(callsign))
                .collect(Collectors.toList());
    }

    @DELETE
    @Path("{callsign}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(LocationInfo location,
                                  @PathParam("callsign") String callsign,
                                  @HeaderParam("authorization")String authHeader)
    {
        log.info("delete(): callsign=\"" + callsign + "\"");
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);

        final String session = EncryptDecrypt.getSession(token);
        locations.removeLocation(callsign, session);
        log.info("delete(): Done");
    }

}
