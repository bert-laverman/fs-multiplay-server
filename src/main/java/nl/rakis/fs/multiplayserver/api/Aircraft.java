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
import nl.rakis.fs.db.Extras;
import nl.rakis.fs.db.Locations;
import nl.rakis.fs.db.UserSessions;
import nl.rakis.fs.security.EncryptDecrypt;

import javax.cache.annotation.CacheResult;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * API for aircraft.
 */
@ApplicationScoped
@Path("aircraft")
public class Aircraft {

    private static final Logger log = Logger.getLogger(Aircraft.class.getName());

    @Inject
    private UserSessions userSessions;
    @Inject
    private nl.rakis.fs.db.Aircraft aircrafts;
    @Inject
    private Locations locations;
    @Inject
    private Extras extras;

    @CacheResult
    private AircraftInfo findAircraft(String callsign, String session) {
        return aircrafts.getAircraftInSession(callsign, session);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<AircraftInfo> getAll(@HeaderParam("authorization")String authHeader)
    {
        log.finest("getAll()");
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);
        UserSessionInfo userSession = userSessions.get(EncryptDecrypt.getSessionId(token));

        log.finest("getAll(): Done");
        return aircrafts.getAllAircraftInSession(userSession.getSession());
    }

    @GET
    @Path("{callsign}")
    @Produces(MediaType.APPLICATION_JSON)
    public AircraftInfo get(@PathParam("callsign") String callsign,
                            @QueryParam("_expand") String expand,
                            @HeaderParam("authorization")String authHeader)
            throws WebApplicationException
    {
        log.finest("get(): callsign=\"" + callsign + "\"");
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);
        UserSessionInfo userSession = userSessions.get(EncryptDecrypt.getSessionId(token));

        final String session = userSession.getSession();
        AircraftInfo result = findAircraft(callsign, session);
        if (result == null) {
            throw new NotFoundException("No such callsign");
        }

        if ((expand != null) && !expand.isEmpty()) {
            String[] fields=expand.split(",");
            for (String field: fields) {
                switch (field) {
                    case "engines":
                        result.setEngines(extras.get(session, callsign, EngineInfo.class));
                        break;
                    case "lights":
                        result.setLights(extras.get(session, callsign, LightInfo.class));
                        break;
                    case "controls":
                        result.setControls(extras.get(session, callsign, ControlsInfo.class));
                        break;
                    case "location":
                        result.setLocation(extras.get(session, callsign, LocationInfo.class));
                        break;
                }
            }
        }
        log.finest("get(): Done");
        return result;
    }

    @PUT
    @Path("{callsign}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AircraftInfo put(AircraftInfo aircraft,
                            @PathParam("callsign") String callsign,
                            @HeaderParam("authorization")String authHeader)
            throws WebApplicationException
    {
        log.finest("put(): callsign=\"" + callsign + "\", aircraft=\"" + aircraft.toString() + "\"");
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);
        UserSessionInfo userSession = userSessions.get(EncryptDecrypt.getSessionId(token));

        final String session = userSession.getSession();
        final String username = userSession.getUsername();
        AircraftInfo current = findAircraft(aircraft.getAtcId(), session);

        if (current == null) {
            throw new NotFoundException("Cannot update non-existent aircraft");
        }
        if (!username.equalsIgnoreCase(current.getUsername()) && !username.equalsIgnoreCase(UserInfo.ADMIN_USER)) {
            throw new NotAuthorizedException("You can only update your own aircraft");
        }

        aircraft.setUsername(username);
        aircraft.setAtcId(callsign);
        aircrafts.setAircraftInSession(aircraft, session);

        log.finest("put(): Done");
        return aircraft;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AircraftInfo post(AircraftInfo aircraft, @HeaderParam("authorization")String authHeader)
            throws WebApplicationException
    {
        log.finest("post(): aircraft=\"" + aircraft.toString() + "\"");
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);
        UserSessionInfo userSession = userSessions.get(EncryptDecrypt.getSessionId(token));

        final String session = userSession.getSession();
        final String username = userSession.getUsername();

        if ((aircraft.getAtcId() == null) || aircraft.getAtcId().equals("")) {
            throw new BadRequestException("Aircraft must have callsign");
        }
        if ((aircraft.getTitle() == null) || aircraft.getTitle().equals("")) {
            throw new BadRequestException("Aircraft must have a 'title'");
        }
        if (findAircraft(aircraft.getAtcId(), session) != null) {
            throw new BadRequestException("Aircraft already exists");
        }

        aircraft.setUsername(username);
        aircrafts.setAircraftInSession(aircraft, session);

        log.finest("post(): Done");
        return aircraft;
    }

    @DELETE
    @Path("{callsign}")
    public void delete(@PathParam("callsign") String callsign, @HeaderParam("authorization")String authHeader)
    {
        log.finest("delete(): callsign=\"" + callsign + "\"");
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);
        UserSessionInfo userSession = userSessions.get(EncryptDecrypt.getSessionId(token));

        final String session = userSession.getSession();
        final String username = userSession.getUsername();

        AircraftInfo current = findAircraft(callsign, session);
        if (current == null) {
            throw new NotFoundException("No such callsign");
        }
        if (!username.equalsIgnoreCase(current.getUsername()) && !username.equalsIgnoreCase(UserInfo.ADMIN_USER)) {
            throw new NotAuthorizedException("You can only remove your own aircraft!");
        }

        locations.removeLocation(callsign, session);
        aircrafts.removeAircraftFromSession(callsign, session);

        log.finest("delete(): Done");
    }
}
