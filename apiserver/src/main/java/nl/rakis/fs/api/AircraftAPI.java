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

import nl.rakis.fs.api.rules.AircraftRules;
import nl.rakis.fs.auth.Token;
import nl.rakis.fs.auth.TokenVerifier;
import nl.rakis.fs.info.AircraftInfo;
import nl.rakis.fs.info.JsonFields;
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

@Path("aircraft")
@RequestScoped
public class AircraftAPI
{

    private static final Logger log = LogManager.getLogger(AircraftAPI.class);

    @Inject
    private TokenVerifier verifier;

    private AircraftRules rules;

    @PostConstruct
    private void init()
    {
        rules = new AircraftRules();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<AircraftInfo> getAircraftList(@HeaderParam("authorization") String authHdr)
    {
        List<AircraftInfo> result = new ArrayList<>();

        result.add(getAircraft("PH-BLA", authHdr));

        return result;
    }

    @GET
    @Path("{callsign}")
    @Produces(MediaType.APPLICATION_JSON)
    public AircraftInfo getAircraft(@PathParam("callsign")        String callsign,
                                    @HeaderParam("authorization") String authHdr)
    {
        Token token = verifier.decodeToken(authHdr);

        AircraftInfo result = new AircraftInfo(callsign);

        rules.cleanRecord(result, token);

        return result;
    }

    @PUT
    @Path("{callsign}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AircraftInfo updateAircraft(@PathParam("callsign")        String callsign,
                                                                     JsonObject aircraft,
                                       @HeaderParam("authorization") String authHdr)
    {
        Token token = verifier.decodeToken(authHdr);

        AircraftInfo result = getAircraft(callsign, authHdr);

        rules.checkUpdate(result, aircraft, token);
        result.updateFromJsonObject(aircraft);

        return result;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String createAircraft(                              JsonObject aircraft,
                                 @HeaderParam("authorization") String authHdr)
    {
        Token token = verifier.decodeToken(authHdr);

        rules.checkCreate(aircraft, token);

        return aircraft.getString(JsonFields.FIELD_ATC_ID);
    }
}
