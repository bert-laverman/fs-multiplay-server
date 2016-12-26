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

import nl.rakis.fs.AircraftStatus;
import nl.rakis.fs.UserData;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Path("user")
public class User {

    private static Map<String, UserData> allAircraft = new HashMap<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<UserData> where()
    {
        return allAircraft.values();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserData where(@PathParam("id") String id) throws WebApplicationException
    {
        UserData result;

        if (!allAircraft.containsKey(id)) {
            throw new NotFoundException("Unknown aircraft");
        }
        else {
            result = allAircraft.get(id);
        }
        return result;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AircraftStatus here(UserData userData) throws WebApplicationException
    {
        AircraftStatus result;

        if (!allAircraft.containsKey(userData.getId())) {
            throw new NotFoundException("Unknown userData");
        }
        else {
            allAircraft.put(userData.getId(), userData);
            result = new AircraftStatus(userData.getId(), "Ok");
        }
        return result;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AircraftStatus add(UserData userData) throws WebApplicationException
    {
        AircraftStatus result;

        if (allAircraft.containsKey(userData.getId())) {
            throw new ForbiddenException("UserData already exists");
        }
        else {
            allAircraft.put(userData.getId(), userData);
            result = new AircraftStatus(userData.getId(), "Ok");
        }
        return result;
    }
}
