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
package nl.rakis.fs;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A user
 */
public class UserInfo
    extends FSData
{
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_SESSION = "session";
    public static final String FIELD_TYPE = "type";
    public static final String USER_TYPE = "User";
    public static final String ADMIN_USER = "admin";

    private String username;
    private String password;
    private String session;

    public UserInfo() {
    }

    public UserInfo(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserInfo(String username, String password, String session) {
        this.username = username;
        this.password = password;
        this.session = session;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInfo)) return false;

        UserInfo userInfo = (UserInfo) o;

        if (getUsername() != null ? !getUsername().equals(userInfo.getUsername()) : userInfo.getUsername() != null)
            return false;
        if (getPassword() != null ? !getPassword().equals(userInfo.getPassword()) : userInfo.getPassword() != null)
            return false;
        return getSession() != null ? getSession().equals(userInfo.getSession()) : userInfo.getSession() == null;
    }

    @Override
    public int hashCode() {
        int result = getUsername() != null ? getUsername().hashCode() : 0;
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (getSession() != null ? getSession().hashCode() : 0);
        return result;
    }

    public static String getType() {
        return USER_TYPE;
    }

    @Override
    public String getKey() {
        return getType()+":"+getUsername();
    }

    @Override
    public Map<String, String> toMap() {
        Map<String,String> result = new HashMap<>();

        result.put(FIELD_TYPE, getType());
        result.put(FIELD_USERNAME, (username == null) ? "" : username);
        result.put(FIELD_PASSWORD, (password == null) ? "" : password);
        result.put(FIELD_SESSION, (session == null) ? "" : session);

        return result;
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObjectBuilder bld = Json.createObjectBuilder()
                .add(FIELD_TYPE, getType())
                .add(FIELD_USERNAME, getUsername())
                .add(FIELD_PASSWORD, getPassword());

        addIf(bld, FIELD_SESSION, getSession());

        return bld.build();
    }

    @Override
    public String toString() {
        return toJsonObject().toString();
    }

    public static UserInfo fromJsonObject(JsonObject obj) {
        UserInfo result = null;

        if (obj != null) {
            result = new UserInfo(obj.getString(UserInfo.FIELD_USERNAME), obj.getString(UserInfo.FIELD_PASSWORD));
            result.setSession(obj.getString(UserInfo.FIELD_SESSION, null));
        }

        return result;
    }

    public static UserInfo fromString(String json) {
        UserInfo result = null;

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

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}
