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
package nl.rakis.fs.multiplayserver;

import com.auth0.jwt.interfaces.DecodedJWT;
import nl.rakis.fs.*;
import nl.rakis.fs.db.Extras;
import nl.rakis.fs.db.UserSessions;
import nl.rakis.fs.security.EncryptDecrypt;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.websocket.*;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.NotAuthorizedException;
import java.io.IOException;
import java.io.StringReader;
import java.util.UUID;
import java.util.logging.Logger;

import static nl.rakis.fs.multiplayserver.ClientSessionHandler.USER_SESSION;

/**
 * Server Endpoint for the WebSocket channel.
 */
@ApplicationScoped
@ServerEndpoint("/live")
public class ClientWebSocketServer {

    private static final Logger log = Logger.getLogger(ClientWebSocketServer.class.getName());

    @Inject
    private ClientSessionHandler sessionHandler;
    @Inject
    private UserSessions userSessions;
    @Inject
    private Extras extras;

    @OnOpen
    public void open(Session session)
    {
        sessionHandler.addWSSession(session);
    }

    @OnClose
    public void close(Session session)
    {
        sessionHandler.removeWSSession(session);
    }

    @OnError
    public void error(Throwable error)
    {
        log.severe("error(): " + error.getMessage());
    }

    private void forceCloseSession(Session session, CloseCodes code, String reason) {
        close(session);
        try {
            session.close(new CloseReason(code, reason));
        } catch (IOException e) {
            //IGNORE
        }
    }

    private boolean checkField(JsonObject obj, String name) {
        return !obj.isNull(name) && !obj.getString(name).isEmpty();
    }

    private void startNewSession(Session session, JsonObject msg)
    {
        DecodedJWT token;
        try {
            token = EncryptDecrypt.decodeToken(msg.getString(JsonFields.FIELD_TOKEN));
            EncryptDecrypt.verifyToken(token);
        } catch (NotAuthorizedException e) {
            forceCloseSession(session, CloseCodes.CANNOT_ACCEPT, "Not authorized");
            return;
        }

        if (!checkField(msg, JsonFields.FIELD_TOKEN)) {
            forceCloseSession(session, CloseCodes.CANNOT_ACCEPT, "No \"token\" in \"hello\"");
            return;
        }
        sessionHandler.addClient(session, userSessions.get(EncryptDecrypt.getSessionId(token)));
    }

    @OnMessage
    public void message(String message, Session session)
    {
        try (JsonReader rd = Json.createReader(new StringReader(message))) {
            JsonObject msg = rd.readObject();

            if (!checkField(msg, JsonFields.FIELD_TYPE)) {
                forceCloseSession(session, CloseCodes.PROTOCOL_ERROR, "No \"type\" in message");
            }
            else {
                final String type = msg.getString(JsonFields.FIELD_TYPE);
                final UserSessionInfo userSession = (UserSessionInfo) session.getUserProperties().get(USER_SESSION);
                final String flySession = userSession.getSession();
                final UUID sessionId = userSession.getSessionId();
                final String callsign = userSession.getCallsign();

                switch (type) {
                    case "hello":
                        startNewSession(session, msg);
                        break;

                    case "add":
                        //FALLTHROUGH
                    case "remove":
                        //IGNORE
                        break;

                    case AircraftInfo.AIRCRAFT_TYPE:
                        sessionHandler.sendToAllInFlySessionButOne(sessionHandler.createReloadMessage(userSession),
                                flySession, sessionId);
                        break;

                    case LocationInfo.LOCATION_TYPE:
                        extras.set(message, flySession, callsign, LocationInfo.class);
                        sessionHandler.sendToAllInFlySessionButOne(message, flySession, sessionId);
                        break;

                    case EngineInfo.TYPE_ENGINES:
                        extras.set(message, flySession, callsign, EngineInfo.class);
                        sessionHandler.sendToAllInFlySessionButOne(message, flySession, sessionId);
                        break;

                    case LightInfo.TYPE_LIGHTS:
                        extras.set(message, flySession, callsign, LightInfo.class);
                        sessionHandler.sendToAllInFlySessionButOne(message, flySession, sessionId);
                        break;

                    case ControlsInfo.TYPE_CONTROLS:
                        extras.set(message, flySession, callsign, ControlsInfo.class);
                        sessionHandler.sendToAllInFlySessionButOne(message, flySession, sessionId);
                        break;

                    default:
                        //IGNORE
                        break;
                }
            }
        }
    }

}
