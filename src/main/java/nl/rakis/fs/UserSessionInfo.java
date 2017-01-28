/*
 * Copyright 2017 Bert Laverman
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
package nl.rakis.fs;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This is what identifies a logged in user.
 */
public class UserSessionInfo
    extends FSData
{
    public static final String USER_SESSION_TYPE = "UserSession";

    private String username;
    private String session;
    private String callsign;
    private UUID sessionId;
    private long lastAccess;

    public UserSessionInfo() {
        sessionId = UUID.randomUUID();
        lastAccess = System.currentTimeMillis();
    }

    public UserSessionInfo(String username, String session, String callsign) {
        this.username = username;
        this.session = session;
        this.callsign = callsign;
        sessionId = UUID.randomUUID();
        lastAccess = System.currentTimeMillis();
    }

    public UserSessionInfo(UUID sessionId, String username, String session, String callsign, long lastAccess) {
        this.username = username;
        this.session = session;
        this.callsign = callsign;
        this.sessionId = sessionId;
        this.lastAccess = lastAccess;
    }

    @Override
    public int hashCode() {
        return sessionId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof UserSessionInfo) ? sessionId.equals(((UserSessionInfo)obj).getSessionId()) : false;
    }

    public static String getType() {
        return USER_SESSION_TYPE;
    }

    @Override
    public String getKey() {
        return sessionId.toString();
    }

    @Override
    public Map<String, String> toMap() {
        HashMap<String,String> result = new HashMap<>();

        result.put(JsonFields.FIELD_TYPE, getType());
        result.put(JsonFields.FIELD_USERNAME, username);
        result.put(JsonFields.FIELD_SESSION, session);
        result.put(JsonFields.FIELD_CALLSIGN, callsign);
        result.put(JsonFields.FIELD_SESSIONID, sessionId.toString());
        result.put(JsonFields.FIELD_LAST_ACCESS, Long.toString(lastAccess));

        return result;
    }

    @Override
    public JsonObject toJsonObject() {

        return Json.createObjectBuilder()
                .add(JsonFields.FIELD_TYPE, getType())
                .add(JsonFields.FIELD_USERNAME, getUsername())
                .add(JsonFields.FIELD_SESSION, getSession())
                .add(JsonFields.FIELD_CALLSIGN, getCallsign())
                .add(JsonFields.FIELD_SESSIONID, getSessionId().toString())
                .add(JsonFields.FIELD_LAST_ACCESS, Long.toString(lastAccess))
                .build();
    }

    public static UserSessionInfo fromJsonObject(JsonObject obj) {
        UserSessionInfo result = null;

        if (obj != null) {
            result = new UserSessionInfo(
                    UUID.fromString(obj.getString(JsonFields.FIELD_SESSIONID)),
                    obj.getString(JsonFields.FIELD_USERNAME),
                    obj.getString(JsonFields.FIELD_SESSION),
                    obj.getString(JsonFields.FIELD_CALLSIGN),
                    Long.parseLong(obj.getString(JsonFields.FIELD_LAST_ACCESS)));
        }

        return result;
    }

    @Override
    public String toString() {
        return toJsonObject().toString();
    }

    public static UserSessionInfo fromString(String json) {
        UserSessionInfo result = null;

        if (json != null) {
            try (StringReader sr = new StringReader(json);
                 JsonReader jr = Json.createReader(sr)) {
                result = fromJsonObject(jr.readObject());
            }
        }
        return result;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public long getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(long lastAccess) {
        this.lastAccess = lastAccess;
    }

    public void resetLastAccess() {
        this.lastAccess = System.currentTimeMillis();
    }
}
