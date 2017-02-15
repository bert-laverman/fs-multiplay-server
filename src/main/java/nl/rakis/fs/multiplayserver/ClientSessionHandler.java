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

import nl.rakis.fs.UserSessionInfo;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.Session;
import java.io.IOException;
import java.util.*;

/**
 * Keep track of the client sessions.
 */
@ApplicationScoped
public class ClientSessionHandler {

    public static final String USER_SESSION = "userSession";
    private final Map<String, Session>         allWSSessions = new HashMap<>();
    private final Map<String, Set<String>>     allSessionMembers = new HashMap<>();
    private final Map<String, UserSessionInfo> allClients = new HashMap<>();

    public void addWSSession(Session session) {
        allWSSessions.put(session.getId(), session);
    }
    public void removeWSSession(Session session) {
        allWSSessions.remove(session.getId());
    }

    public List<UserSessionInfo> getClients() {
        return new ArrayList<>(allClients.values());
    }

    public void addClient(Session session, UserSessionInfo userSession) {
        allClients.put(userSession.getKey(), userSession);

        final String flySession = userSession.getSession();

        if (allSessionMembers.containsKey(flySession)) {
            allSessionMembers.get(flySession).add(userSession.getKey());
        }
        else {
            Set<String> newSession = new HashSet<>();
            newSession.add(userSession.getKey());
            allSessionMembers.put(flySession, newSession);
        }

        // store callsign
        session.getUserProperties().put(USER_SESSION, userSession);

        sendToAllInFlySessionButOne(createAddMessage(userSession), flySession, userSession.getSessionId());
    }

    public void removeClient(UserSessionInfo userSession) {
        final String flySession = userSession.getSession();
        if (allSessionMembers.containsKey(flySession)) {
            allSessionMembers.get(flySession).remove(userSession.getCallsign());
        }
        final String key = userSession.getKey();
        if (allClients.containsKey(key)) {
            UserSessionInfo client = allClients.get(key);

            allClients.remove(key);

            sendToAllInFlySession(createRemoveMessage(client), flySession);
        }
    }

    private JsonObject createAddMessage(UserSessionInfo userSession) {
        return Json.createObjectBuilder()
                .add("type", "add")
                .add("session", userSession.getSession())
                .add("callsign", userSession.getCallsign())
                .build();
    }

    private JsonObject createRemoveMessage(UserSessionInfo userSession) {
        return Json.createObjectBuilder()
                .add("type", "remove")
                .add("session", userSession.getSession())
                .add("callsign", userSession.getCallsign())
                .build();
    }

    public JsonObject createReloadMessage(UserSessionInfo userSession) {
        return Json.createObjectBuilder()
                .add("type", "reload")
                .add("callsign", userSession.getCallsign())
                .build();
    }

    private void sendToSession(Session session, JsonObject message) {
        try {
            session.getBasicRemote().sendText(message.toString());
        }
        catch (IOException e) {
            //TODO Check what to do
            allWSSessions.remove(session);
        }
    }

    public void sendToAllInFlySession(JsonObject message, String flySession) {
        for (Session wsSession: allWSSessions.values()) {
            final UserSessionInfo sess = (UserSessionInfo) wsSession.getUserProperties().get(USER_SESSION);
            if ((sess == null) || !sess.getSession().equals(flySession)) {
                continue;
            }
            sendToSession(wsSession, message);
        }
    }

    public void sendToAllInFlySessionButOne(JsonObject message, String flySession, UUID sessionId) {
        for (Session wsSession: allWSSessions.values()) {
            final UserSessionInfo sess = (UserSessionInfo) wsSession.getUserProperties().get(USER_SESSION);
            if ((sess == null) || !sess.getSession().equals(flySession)) {
                continue;
            }
            if (sessionId.equals(sess.getSessionId())) {
                continue;
            }
            sendToSession(wsSession, message);
        }
    }
}
