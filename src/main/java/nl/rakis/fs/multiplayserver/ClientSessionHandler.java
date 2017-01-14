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

    private final Map<String, Session>     allWSSessions = new HashMap<>();
    private final Map<String, Set<String>> allSessionMembers = new HashMap<>();
    private final Map<String, Client>      allClients = new HashMap<>();

    public void addWSSession(Session session) {
        allWSSessions.put(session.getId(), session);
    }
    public void removeWSSession(Session session) {
        allWSSessions.remove(session.getId());
    }

    public List<Client> getClients() {
        return new ArrayList<>(allClients.values());
    }

    public void addClient(String session, String callsign, Session wsSession) {
        Client client = new Client(session, callsign, wsSession.getId());

        allClients.put(client.getKey(), client);
        if (allSessionMembers.containsKey(session)) {
            allSessionMembers.get(session).add(callsign);
        }
        else {
            Set<String> newSession = new HashSet<>();
            newSession.add(callsign);
            allSessionMembers.put(session, newSession);
        }

        // store callsign
        wsSession.getUserProperties().put("callsign", callsign);
        wsSession.getUserProperties().put("session", session);

        sendToAllConnectedSessionsButOne(createAddMessage(client), session, callsign);
    }

    public void removeClient(String session, String callsign) {
        if (allSessionMembers.containsKey(session)) {
            allSessionMembers.get(session).remove(callsign);
        }
        final String key = session + ":" + callsign;
        if (allClients.containsKey(key)) {
            Client client = allClients.get(key);

            allClients.remove(key);

            sendToAllConnectedSessions(createRemoveMessage(client), session);
        }
    }

    private JsonObject createAddMessage(Client client) {
        return Json.createObjectBuilder()
                .add("type", "add")
                .add("session", client.getSession())
                .add("callsign", client.getCallsign())
                .build();
    }

    private JsonObject createRemoveMessage(Client client) {
        return Json.createObjectBuilder()
                .add("type", "remove")
                .add("session", client.getSession())
                .add("callsign", client.getCallsign())
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

    public void sendToAllConnectedSessions(JsonObject message, String session) {
        for (Session wsSession: allWSSessions.values()) {
            final String sess = wsSession.getUserProperties().get("session").toString();
            if ((sess == null) || !sess.equals(session)) {
                continue;
            }
            sendToSession(wsSession, message);
        }
    }

    public void sendToAllConnectedSessionsButOne(JsonObject message, String session, String notThisOne) {
        for (Session wsSession: allWSSessions.values()) {
            final String sess = wsSession.getUserProperties().get("session").toString();
            if ((sess == null) || !sess.equals(session)) {
                continue;
            }
            final String callsign = wsSession.getUserProperties().get("callsign").toString();
            if ((callsign == null) || callsign.equals(notThisOne)) {
                continue;
            }
            sendToSession(wsSession, message);
        }
    }
}
