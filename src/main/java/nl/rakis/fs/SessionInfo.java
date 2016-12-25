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
import java.util.List;
import java.util.Map;

/**
 * Created by bertl on 12/18/2016.
 */
public class SessionInfo
    implements FSData
{
    private String id;
    private String name;
    private String description;
    private List<UserInfo> users;

    public SessionInfo() {

    }

    public SessionInfo(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public SessionInfo(String id, String name, String description, List<UserInfo> users) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.users = users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SessionInfo)) return false;

        SessionInfo that = (SessionInfo) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        return users != null ? users.equals(that.users) : that.users == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (users != null ? users.hashCode() : 0);
        return result;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new SessionInfo(id, name, description, users);
    }

    public SessionInfo cleanClone() {
        return new SessionInfo(id, name, description);
    }

    @Override
    public Map<String, String> asMap() {
        HashMap<String,String> result = new HashMap<>();

        result.put("id", id);
        result.put("name", (name == null) ? "" : name);
        result.put("description", (description == null) ? "" : description);

        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getNumberOfUsers() {
        return users.size();
    }

}
