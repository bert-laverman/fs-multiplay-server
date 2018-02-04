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
package nl.rakis.fs.info;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Multiplayer session
 */
public class SessionInfo
    extends FSData
{
    public static final String SESSION_TYPE = "Session";

    private String name;
    private String description;
    private boolean openSession;

    private List<String> members = new ArrayList<>();
    private List<String> aircraft = new ArrayList<>();

    public SessionInfo() {
    }

    public SessionInfo(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SessionInfo)) return false;

        SessionInfo that = (SessionInfo) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return (description != null ? !description.equals(that.description) : that.description != null);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        SessionInfo newSession = new SessionInfo();

        newSession.setName(getName());
        newSession.setDescription(getDescription());

        return newSession;
    }

    public SessionInfo cleanClone() {
        SessionInfo newSession = new SessionInfo();

        newSession.setName(getName());
        newSession.setDescription(getDescription());

        return newSession;
    }

    public static String getType() {
        return SESSION_TYPE;
    }

    @Override
    public String getKey() {
        return getType()+":"+getName();
    }

    @Override
    public Map<String, String> toMap() {
        HashMap<String,String> result = new HashMap<>();

        result.put(JsonFields.FIELD_TYPE, getType());
        result.put(JsonFields.FIELD_NAME, name);
        result.put(JsonFields.FIELD_DESCRIPTION, (description == null) ? "" : description);

        return result;
    }

    @Override
    public JsonObject toJsonObject() {
        return Json.createObjectBuilder()
                .add(JsonFields.FIELD_TYPE, getType())
                .add(JsonFields.FIELD_NAME, getName())
                .add(JsonFields.FIELD_DESCRIPTION, (description == null) ? "" : description)
                .add(JsonFields.FIELD_AIRCRAFT, toArray(aircraft))
                .build();
    }

    @Override
    public String toString() {
        return toJsonObject().toString();
    }

    @Override
    public void updateFromJsonObject(JsonObject obj) {
        if (!obj.isNull(JsonFields.FIELD_NAME)) {
            setName(obj.getString(JsonFields.FIELD_NAME));
        }
        if (!obj.isNull(JsonFields.FIELD_DESCRIPTION)) {
            setName(obj.getString(JsonFields.FIELD_DESCRIPTION));
        }
    }

    public static SessionInfo fromJsonObject(JsonObject obj) {
        SessionInfo result = null;

        if (obj != null) {
            result = new SessionInfo(obj.getString(JsonFields.FIELD_NAME), obj.getString(JsonFields.FIELD_DESCRIPTION));
        }

        return result;
    }

    public static SessionInfo fromString(String json) {
        SessionInfo result = null;

        if (json != null) {
            try (StringReader sr = new StringReader(json);
                 JsonReader jr = Json.createReader(sr)) {
                result = fromJsonObject(jr.readObject());
            }
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOpenSession() {
        return openSession;
    }

    public void setOpenSession(boolean openSession) {
        this.openSession = openSession;
    }

    public List<String> getAircraft() {
        return aircraft;
    }

    public void setAircraft(List<String> aircraft) {
        this.aircraft = aircraft;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
