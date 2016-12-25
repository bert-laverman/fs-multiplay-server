package nl.rakis.fs.multiplayserver.resources;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import nl.rakis.fs.AuthenticationInfo;
import nl.rakis.fs.NamedObject;
import nl.rakis.fs.SessionInfo;
import nl.rakis.fs.security.EncryptDecrypt;

import javax.cache.annotation.CacheResult;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("session")
public class Session {

    private static Map<String,SessionInfo> allSessions = new HashMap<>();

    static {
        RedisClient rc = RedisClient.create("redis://smb:6379/0");
        StatefulRedisConnection<String,String> connection = rc.connect();
        RedisCommands<String,String> cmd = connection.sync();

        if (cmd.setnx("initDone", "true")) {
            SessionInfo session = new SessionInfo(UUID.randomUUID().toString(), "Admin Session", "Dummy session for admin users");
            cmd.hmset(session.getId(), session.asMap());

            session = new SessionInfo(UUID.randomUUID().toString(), "Waiting Room Session", "Dummy session for normal users");
            cmd.hmset(session.getId(), session.asMap());
        }
        connection.close();
        rc.shutdown();
    }

    @CacheResult
    private SessionInfo findSessionByName(String name)
    {
        SessionInfo result = null;

        for (SessionInfo session : allSessions.values()) {
            if (session.getName().equalsIgnoreCase(name)) {
                result = session;
                break;
            }
        }
        return result;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<SessionInfo> allSessions(@HeaderParam("authorization")String authHeader)
    {
        System.err.println("GET all sessions");
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);

        return allSessions.values().stream()
                .map(session -> session.cleanClone())
                .collect(Collectors.toList());
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public SessionInfo session(@PathParam("id") String id, @HeaderParam("authorization")String authHeader)
    {
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);
        String mySession = EncryptDecrypt.getSession(token);

        SessionInfo result;

        if (!mySession.equals(id)) {
            throw new NotAuthorizedException(("Not your session"));
        }
        else {
            if (!allSessions.containsKey(id)) {
                throw new NotFoundException("Unknown session");
            } else {
                result = allSessions.get(id);
            }
        }
        return result;
    }

    @POST
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public SessionInfo newSession(NamedObject newSession, @HeaderParam("authorization")String authHeader)
    {
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);

        if (findSessionByName(newSession.getName()) != null) {
            throw new ForbiddenException("Session with that name already exists");
        }
        SessionInfo session = new SessionInfo();
        session.setId(UUID.randomUUID().toString());
        session.setName(newSession.getName());
        session.setDescription(newSession.getDescription());

        allSessions.put(session.getId(), session);

        return session;
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SessionInfo changeDetails(@NotNull @PathParam("id") String id, SessionInfo session, @HeaderParam("authorization")String authHeader)
    {
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);

        if (!allSessions.containsKey(id)) {
            throw new NotFoundException();
        }
        if (!id.equals(session.getId())) {
            throw new BadRequestException("Session id mismatch");
        }
        SessionInfo result = allSessions.get(session.getId());

        result.setDescription(session.getDescription());
        if (!result.getName().equalsIgnoreCase(session.getName())) {
            // Be careful changing the name
            if (findSessionByName(session.getName()) != null) {
                throw new ForbiddenException("Session with that name already exists");
            }
            result.setName(session.getName());
        }

        return result;
    }

    @DELETE
    @Path("{id}")
    public void removeSession(@NotNull @PathParam("id") String id, @HeaderParam("authorization")String authHeader)
    {
        DecodedJWT token = EncryptDecrypt.decodeToken(authHeader);
        EncryptDecrypt.verifyToken(token);

        if (EncryptDecrypt.getSession(token).equals(id)) {
            throw new ForbiddenException("Cannot remove the session you're in");
        }
        if (!allSessions.containsKey(id)) {
            throw new NotFoundException();
        }
        allSessions.remove(id);
    }

    @PUT
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String login(AuthenticationInfo authInfo)
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
        SessionInfo session = findSessionByName(authInfo.getSession());
        if (session == null) {
            throw new NotFoundException("Session not found");
        }
        authInfo.setSession(session.getId());
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
