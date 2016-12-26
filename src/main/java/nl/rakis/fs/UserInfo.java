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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by bertl on 12/18/2016.
 */
public class UserInfo
    implements FSData
{
    private String id;
    private String username;
    private String password;
    private String session;

    public UserInfo() {
    }

    public UserInfo(String username, String password) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInfo)) return false;

        UserInfo userInfo = (UserInfo) o;

        if (getId() != null ? !getId().equals(userInfo.getId()) : userInfo.getId() != null) return false;
        if (getUsername() != null ? !getUsername().equals(userInfo.getUsername()) : userInfo.getUsername() != null)
            return false;
        if (getPassword() != null ? !getPassword().equals(userInfo.getPassword()) : userInfo.getPassword() != null)
            return false;
        return getSession() != null ? getSession().equals(userInfo.getSession()) : userInfo.getSession() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (getSession() != null ? getSession().hashCode() : 0);
        return result;
    }

    @Override
    public String getType() {
        return "User";
    }

    @Override
    public Map<String, String> asMap() {
        HashMap<String,String> result = new HashMap<>();

        result.put("id", id);
        result.put("type", "User");
        result.put("username", (username == null) ? "" : username);
        result.put("password", (password == null) ? "" : password);
        result.put("sesion", (session == null) ? "" : session);

        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
