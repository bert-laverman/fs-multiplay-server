package nl.rakis.fs.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    private String path;
    private Map<String,Set<String>> groups = new HashMap<>();
    private Map<String,Set<String>> users = new HashMap<>();

    public GroupFile(String path)
    {
        log.debug("GroupFile(\"" + path + "\")");

        this.path = path;
    }

    private void load()
    {
        log.trace("load()");

        if (groups.size() == 0) {
            if (log.isDebugEnabled()) {
                log.debug("load(): Reading \"" + path + "\"");
            }
            try {
                try (FileReader fr = new FileReader(path);
                     BufferedReader br=new BufferedReader(fr))
                {
                    if (log.isTraceEnabled()) {
                        log.trace("load(): Userlist \"" + path + "\" opened");
                    }
                    for (String line = br.readLine(); line != null; line = br.readLine()) {
                        if (log.isTraceEnabled()) {
                            log.trace("load(): Read \"" + line + "\"");
                        }

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
                        final HashSet userSet = new HashSet(Arrays.asList(users));

                        if (log.isDebugEnabled()) {
                            log.debug("load(): Adding group \"" + group + "\" with users " + userSet);
                        }
                        groups.put(group, userSet);
                    }
                }
            }
            catch (IOException e) {
                groups.clear();

                log.error("load(): Failed to load \"" + path + "\"", e);
            }
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
    }

    public Map<String,Set<String>> getGroups() {
        load();
        return groups;
    }

    public Set<String> getUsersInGroup(String group) {
        return getGroups().containsKey(group) ? groups.get(group) : new HashSet<>();
    }

    public Map<String,Set<String>> getUsers() {
        load();
        return users;
    }

    public Set<String> getGroupsForUser(String user) {
        return getUsers().containsKey(user) ? users.get(user) : new HashSet<>();
    }

    public boolean isUserInGroup(String user, String group) {
        return getUsersInGroup(group).contains(user);
    }
}
