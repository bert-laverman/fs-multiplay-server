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
import nl.rakis.fs.db.UserSessions;
import nl.rakis.fs.security.EncryptDecrypt;

import javax.cache.annotation.CacheResult;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
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
    private Extras extras;

    @Context
    private UriInfo uriInfo;

    @CacheResult
    private AircraftInfo findAircraft(String session, String callsign) {
        return aircrafts.getAircraftInSession(session, callsign);
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
        AircraftInfo result = findAircraft(session, callsign);
        if (result == null) {
            throw new NotFoundException("No such callsign");
        }

        if ((expand != null) && !expand.isEmpty()) {
            log.finest("get(): _expand=\"" + expand + "\"");
            String[] fields=expand.split(",");
            for (String field: fields) {
                log.finest("get(): field=\"" + field + "\"");
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
        log.finest("get(): Done, value=\"" + result.toString() + "\"");
        return result;
    }

    @PUT
    @Path("{callsign}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response put(AircraftInfo aircraft,
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

        AircraftInfo current = findAircraft(session, aircraft.getAtcId());
        if (current != null) {
            if (!username.equalsIgnoreCase(current.getUsername()) && !username.equalsIgnoreCase(UserInfo.ADMIN_USER)) {
                throw new NotAuthorizedException("You can only update your own aircraft");
            }
        }

        aircraft.setUsername(username);
        aircraft.setAtcId(callsign);
        final URI uri = uriInfo.getAbsolutePath();
        aircraft.setHref(uri.toString());

        aircrafts.setAircraftInSession(session, aircraft);

        if (aircraft.getLocation() != null) {
            extras.set(aircraft.getLocation(), session, callsign);
        }
        if (aircraft.getEngines() != null) {
            extras.set(aircraft.getEngines(), session, callsign);
        }
        if (aircraft.getLights() != null) {
            extras.set(aircraft.getLights(), session, callsign);
        }
        if (aircraft.getControls() != null) {
            extras.set(aircraft.getControls(), session, callsign);
        }

        return ((current == null) ? Response.created(uri) : Response.ok())
                .entity(aircraft)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(AircraftInfo aircraft, @HeaderParam("authorization")String authHeader)
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
        if (findAircraft(session, aircraft.getAtcId()) != null) {
            throw new BadRequestException("Aircraft already exists");
        }

        final String callsign = aircraft.getAtcId();
        aircraft.setUsername(username);
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        uriBuilder.path(callsign);
        final URI uri = uriBuilder.build();
        aircraft.setHref(uri.toString());

        aircrafts.setAircraftInSession(session, aircraft);

        if (aircraft.getLocation() != null) {
            extras.set(aircraft.getLocation(), session, callsign);
        }
        if (aircraft.getEngines() != null) {
            extras.set(aircraft.getEngines(), session, callsign);
        }
        if (aircraft.getLights() != null) {
            extras.set(aircraft.getLights(), session, callsign);
        }
        if (aircraft.getControls() != null) {
            extras.set(aircraft.getControls(), session, callsign);
        }

        log.finest("post(): Done");

        return Response.created(uri).build();
    }

    @DELETE
    @Path("{callsign}")
    public Response delete(@PathParam("callsign") String callsign, @HeaderParam("authorization")String authHeader)
    {
        log.info("delete(): callsign=\"" + callsign + "\"");
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);
        UserSessionInfo userSession = userSessions.get(EncryptDecrypt.getSessionId(token));

        final String session = userSession.getSession();
        final String username = userSession.getUsername();

        AircraftInfo current = findAircraft(session, callsign);
        if (current == null) {
            throw new NotFoundException("No such callsign");
        }
        if (!username.equalsIgnoreCase(current.getUsername()) && !username.equalsIgnoreCase(UserInfo.ADMIN_USER)) {
            throw new NotAuthorizedException("You can only remove your own aircraft!");
        }

        try {
            extras.remove(session, callsign, LocationInfo.class);
            extras.remove(session, callsign, LightInfo.class);
            extras.remove(session, callsign, EngineInfo.class);
            extras.remove(session, callsign, ControlsInfo.class);
            aircrafts.removeAircraftFromSession(session, callsign);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        log.finest("delete(): Done");
        return Response.noContent().build();
    }
}