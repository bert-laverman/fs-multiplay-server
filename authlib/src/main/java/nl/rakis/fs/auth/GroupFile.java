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
                String[] grpFields = line.split(":");
                if ((grpFields == null) || (grpFields.length != NUM_FIELDS))
                {
                    throw new IOException("Bad group format: \"" + line + "\"");
                }

                checkNonEmpty(FLDNAME_GROUPNAME, grpFields [FLD_USERNAME]);
                String group = grpFields [FLD_USERNAME];
                checkEmpty(FLDNAME_PASSWORD, grpFields [FLD_PASSWORD]);
                checkEmpty(FLDNAME_GROUPID, grpFields [FLD_GROUPID]);
                checkNonEmpty(FLDNAME_USERS, grpFields [FLD_USERS]);

                String[] users = grpFields [FLD_USERS].split(",");
                final HashSet<String> userSet = new HashSet<>(Arrays.asList(users));

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

        if (!getUsers().containsKey(userName)) {
            if (log.isInfoEnabled()) {
                log.info("addUserToGroup(): Adding new group \"" + groupName + "\"");
            }
            groups.put(groupName, new HashSet<String>(Collections.singleton(userName)));
            result = true;
        }
        else {
            getUsersInGroup(groupName).add(userName);
            result = store();
        }
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

        if (!getUsers().containsKey(userName)) {
            log.error("removeUserFromGroup(): Unknown group \"" + groupName + "\"");
        }
        else {
            Set<String> usersInGroup = getUsersInGroup(groupName);
            if (!usersInGroup.contains(userName)) {
                log.warn("removeUserFromGroup(): User \"" + userName + "\" wasn't in group \"" + groupName + "\" to begin with");
                result = true;
            }
            else {
                usersInGroup.remove(userName);
                getGroupsForUser(userName).remove(groupName);
                result = store();
            }
        }
        if (log.isInfoEnabled()) {
            log.info("removeUserFromGroup(): " + (result ? "Success" : "Failed"));
        }
        return result;
    }
        
}
