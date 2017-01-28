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
import nl.rakis.fs.JsonFields;
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
import java.util.logging.Logger;

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

    @OnOpen
    public void open(Session session)
    {
        log.info("open()");
        sessionHandler.addWSSession(session);
    }

    @OnClose
    public void close(Session session)
    {
        log.info("close()");
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
        log.info("message()");
        try (JsonReader rd = Json.createReader(new StringReader(message))) {
            JsonObject msg = rd.readObject();

            if (!checkField(msg, JsonFields.FIELD_TYPE)) {
                forceCloseSession(session, CloseCodes.PROTOCOL_ERROR, "No \"type\" in message");
            }
            else {
                final String type = msg.getString(JsonFields.FIELD_TYPE);
                switch (type) {
                    case "hello":
                        startNewSession(session, msg);
                        break;

                    case "add":
                        //FALLTHROUGH
                    case "remove":
                        //IGNORE
                        break;

                    default:
                        //IGNORE
                        break;
                }
            }
        }
    }

}