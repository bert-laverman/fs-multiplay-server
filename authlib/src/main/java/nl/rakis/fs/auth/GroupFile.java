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
package nl.rakis.fs.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

public class GroupFile extends SecurityFile {

    private static final Logger log = LogManager.getLogger(GroupFile.class);

    public static final int NUM_FIELDS = 4;
    public static final String FLDNAME_GROUPNAME = "Groupname";
    public static final int FLD_USERNAME = 0;
    public static final String FLDNAME_PASSWORD = "Password";
    public static final int FLD_PASSWORD = 1;
    public static final String FLDNAME_GROUPID = "Group ID";
    public static final int FLD_GROUPID = 2;
    public static final String FLDNAME_USERS = "Users";
    public static final int FLD_USERS = 3;

    private File path;
    private Map<String,Set<String>> groups = new HashMap<>();
    private Map<String,Set<String>> users = new HashMap<>();

    public GroupFile(String path)
    {
        log.debug("GroupFile(\"" + path + "\")");

        this.path = new File(path);
    }

    public GroupFile(File path)
    {
        log.debug("GroupFile(\"" + path.getAbsolutePath() + "\")");

        this.path = path;
    }

    private void load() {
        if (log.isDebugEnabled()) {
            log.debug("load()");
        }

        load(path, (String line) -> {
            boolean result = false;

            try {
                List<String> grpFields = split(line);
                if (grpFields.size() != NUM_FIELDS) {
                    throw new IOException("Bad group format: \"" + line + "\"");
                }

                checkNonEmpty(FLDNAME_GROUPNAME, grpFields.get(FLD_USERNAME));
                String group = grpFields.get(FLD_USERNAME);
                checkEmpty(FLDNAME_PASSWORD, grpFields.get(FLD_PASSWORD));
                checkEmpty(FLDNAME_GROUPID, grpFields.get(FLD_GROUPID));

                final String usrField = grpFields.get(FLD_USERS);
                final HashSet<String> userSet =
                 (usrField.trim().isEmpty())
                  ? new HashSet<>()
                  : new HashSet<>(split(usrField, ','));

                if (log.isDebugEnabled()) {
                    log.debug("load(): Adding group \"" + group + "\" with users " + userSet);
                }
                groups.put(group, userSet);
            }
            catch (IOException e) {
                log.error("load(): Exception while loading \"" + path + "\"", e);
                result = true;
                groups.clear();
            }
            return result;
        });
        if (log.isDebugEnabled()) {
            log.debug("load(): " + groups.size() + " group(s) read");
        }

        // Create reverse mapping
        for (String group: groups.keySet()) {
            Set<String> usersList = groups.get(group);

            if (users != null) {
                for (String user: usersList) {
                    if (users.containsKey(user)) {
                        users.get(user).add(group);
                    }
                    else {
                        Set<String> groupsOfUser = new HashSet<>();
                        groupsOfUser.add(group);
                        users.put(user, groupsOfUser);
                    }
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("load(): " + users.size() + " users(s) mapped to groups");
        }
    }

    private boolean store() {
        return store("groups", path, (PrintWriter pr) -> {
            for (String groupName: groups.keySet()) {
                Set<String> users = groups.get(groupName);
                pr.print(groupName);
                pr.print(":x:x:");
                boolean first = true;
                for (String userName: users) {
                    if (first) {
                        first = false;
                    }
                    else {
                        pr.print(",");
                    }
                    pr.print(userName);
                }
                pr.println();
            }
        });
    }

    public Map<String,Set<String>> getGroups() {
        if (groups.size() == 0) {
            load();
        }
        return groups;
    }

    public Set<String> getUsersInGroup(String group) {
        return getGroups().containsKey(group) ? groups.get(group) : new HashSet<>();
    }

    public Map<String,Set<String>> getUsers() {
        if (users.size() == 0) {
            load();
        }
        return users;
    }

    public boolean addGroup(String groupName) {
        if (log.isInfoEnabled()) {
            log.info("addGroup(): Adding group \"" + groupName + "\"");
        }

        boolean result = false;

        if (!getGroups().containsKey(groupName)) {
            if (log.isInfoEnabled()) {
                log.info("addGroup(): Adding new group \"" + groupName + "\"");
            }
            groups.put(groupName, new HashSet<String>());
            result = store();
        }
        if (log.isInfoEnabled()) {
            log.info("addGroup(): " + (result ? "Success" : "Failed"));
        }
        return result;
    }

    public boolean removeGroup(String groupName) {
        if (log.isInfoEnabled()) {
            log.info("removeGroup(): Removing group \"" + groupName + "\"");
        }

        boolean result = false;

        if (getGroups().containsKey(groupName)) {
            if (log.isInfoEnabled()) {
                log.info("removeGroup(): Removing group \"" + groupName + "\"");
            }
            groups.remove(groupName);
            for (Set<String> groups: users.values()) {
                if (groups.contains(groupName)) {
                    groups.remove(groupName);
                }
            }
            result = store();
        }
        if (log.isInfoEnabled()) {
            log.info("removeGroup(): " + (result ? "Success" : "Failed"));
        }
        return result;
    }

    public Set<String> getGroupsForUser(String user) {
        return getUsers().containsKey(user) ? users.get(user) : new HashSet<>();
    }

    public boolean isUserInGroup(String user, String group) {
        return getUsersInGroup(group).contains(user);
    }

    public boolean addUserToGroup(String userName, String groupName) {
        if (log.isInfoEnabled()) {
            log.info("addUserToGroup(): Adding user \"" + userName + "\" to group \"" + groupName + "\"");
        }

        boolean result = false;

        // Fix the group list
        if (!getGroups().containsKey(groupName)) {
            if (log.isInfoEnabled()) {
                log.info("addUserToGroup(): Adding new group \"" + groupName + "\"");
            }
            groups.put(groupName, new HashSet<String>(Collections.singleton(userName)));
        }
        else {
            if (log.isDebugEnabled()) {
                log.debug("addUserToGroup(): Adding user \"" + userName + "\" to existing group \"" + groupName + "\"");
            }
            groups.get(groupName).add(userName);
        }

        // fix the user (aka reverse lookup) list
        if (!users.containsKey(userName)) {
            if (log.isDebugEnabled()) {
                log.debug("addUserToGroup(): Adding new user \"" + userName + "\" to reverse lookup list");
            }
            users.put(userName, new HashSet<String>(Collections.singleton(groupName)));
        }
        else {
            if (log.isDebugEnabled()) {
                log.debug("addUserToGroup(): Adding group \"" + groupName + "\" to existing user \"" + userName + "\" reverse lookup list");
            }
            users.get(userName).add(groupName);
        }
        result = store();
        if (log.isInfoEnabled()) {
            log.info("addUserToGroup(): " + (result ? "Success" : "Failed"));
        }
        return result;
    }

    public boolean removeUserFromGroup(String userName, String groupName) {
        if (log.isInfoEnabled()) {
            log.info("removeUserFromGroup(): Removing user \"" + userName + "\" from group \"" + groupName + "\"");
        }

        boolean result = false;

        if (!getGroups().containsKey(groupName)) {
            log.error("removeUserFromGroup(): Unknown group \"" + groupName + "\"");
        }
        else {
            groups.get(groupName).remove(userName);
        }
        if (!users.containsKey(userName)) {
            log.warn("removeUserFromGroup(): User \"" + userName + "\" wasn't in any group to begin with");
        }
        else {
            users.get(userName).remove(groupName);
        }
        result = store();
        if (log.isInfoEnabled()) {
            log.info("removeUserFromGroup(): " + (result ? "Success" : "Failed"));
        }
        return result;
    }
        
}
