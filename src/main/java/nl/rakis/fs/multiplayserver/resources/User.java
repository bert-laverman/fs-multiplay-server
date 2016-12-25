package nl.rakis.fs.multiplayserver.resources;

import nl.rakis.fs.AircraftStatus;
import nl.rakis.fs.UserInfo;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Path("user")
public class User {

    private static Map<String, UserInfo> allAircraft = new HashMap<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<UserInfo> where()
    {
        return allAircraft.values();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserInfo where(@PathParam("id") String id) throws WebApplicationException
    {
        UserInfo result;

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
    public AircraftStatus here(UserInfo userInfo) throws WebApplicationException
    {
        AircraftStatus result;

        if (!allAircraft.containsKey(userInfo.getId())) {
            throw new NotFoundException("Unknown userInfo");
        }
        else {
            allAircraft.put(userInfo.getId(), userInfo);
            result = new AircraftStatus(userInfo.getId(), "Ok");
        }
        return result;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AircraftStatus add(UserInfo userInfo) throws WebApplicationException
    {
        AircraftStatus result;

        if (allAircraft.containsKey(userInfo.getId())) {
            throw new ForbiddenException("UserInfo already exists");
        }
        else {
            allAircraft.put(userInfo.getId(), userInfo);
            result = new AircraftStatus(userInfo.getId(), "Ok");
        }
        return result;
    }
}
