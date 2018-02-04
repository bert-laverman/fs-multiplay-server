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

import java.io.Console;
import java.io.File;

public class AuthUtil
{

    private static File dirPath = new File(".");

    private static void usage() {
        System.err.println("Usage:");
        System.err.println("  authutil [-d|--dir <path>] adduser <username> <real-name> <default-session>");
        System.err.println("  authutil [-d|--dir <path>] rmuser <username>");
        System.err.println("  authutil [-d|--dir <path>] passwd <username>");
        System.err.println("  authutil [-d|--dir <path>] basicauth <username>");
        System.err.println("  authutil [-d|--dir <path>] unbasicauth <base64-token>");
        System.err.println("  authutil [-d|--dir <path>] addgroup <groupname>");
        System.err.println("  authutil [-d|--dir <path>] rmgroup <groupname>");
        System.err.println("  authutil [-d|--dir <path>] addgroupuser <groupname> <username>");
        System.err.println("  authutil [-d|--dir <path>] rmgroupuser <groupname> <username>");
        System.err.println("  authutil [-d|--dir <path>] showuser <username>");
        System.err.println("  authutil [-d|--dir <path>] showgroup <groupname>");
        System.err.println();
        System.err.println("  -d|--dir     Set the directory for the data files (default \".\"");
        System.err.println("Usage:");
    }

    private static void checkParmCount(int i, int len, int npar) throws Exception {
        if ((len - i) < (npar + 1)) {
            throw new Exception("Bad number of parameters");
        }
    }

    private static void addUser(String userName, String realName, String defSession) {
        PasswordFile f = new PasswordFile(new File(dirPath, "passwd"));
        if (f.addUser(userName, realName, defSession)) {
            System.err.println("User \"" + userName + "\" added");
        }
        else {
            System.err.println("Failed to add user \"" + userName + "\"");
        }
    }

    private static void rmUser(String userName) {
        PasswordFile fp = new PasswordFile(new File(dirPath, "passwd"));
        if (fp.removeUser(userName)) {
            System.err.println("User \"" + userName + "\" removed");
        }
        else {
            System.err.println("Failed to remove user \"" + userName + "\"");
        }
        ShadowFile fs = new ShadowFile(new File(dirPath, "shadow"));
        if (fs.removePassword(userName)) {
            System.err.println("Password for user \"" + userName + "\" removed");
        }
        else {
            System.err.println("Failed to remove password for user \"" + userName + "\"");
        }
    }

    private static void passwd(String userName) {
        Console con = System.console();
        if (con == null) {
            System.err.println("Failed to open console for input");
            System.exit(-2);
        }
        PasswordFile fp = new PasswordFile(new File(dirPath, "passwd"));
        if (fp.getUser(userName) == null) {
            System.err.println("User \"" + userName + "\" unknown");
        }
        else {
            System.out.print("Enter password: ");
            String p1 = new String(con.readPassword());
            System.out.print("Re-enter password: ");
            String p2 = new String(con.readPassword());
            if (!p1.equals(p2)) {
                System.err.println("Passwords do not match");
                System.exit(-3);
            }
            ShadowFile fs = new ShadowFile(new File(dirPath, "shadow"));
            if (fs.setPassword(userName, p1)) {
                System.err.println("Password for user \"" + userName + "\" set");
            }
            else {
                System.err.println("Failed to set password for user \"" + userName + "\"");
            }
        }
    }

    private static void basicAuth(String userName) {
        Console con = System.console();
        if (con == null) {
            System.err.println("Failed to open console for input");
            System.exit(-2);
        }
        System.out.print("Enter password: ");
        String p1 = new String(con.readPassword());
        System.out.print("Re-enter password: ");
        String p2 = new String(con.readPassword());
        if (!p1.equals(p2)) {
            System.err.println("Passwords do not match");
            System.exit(-3);
        }

        System.err.println(new BasicAuth(userName, p1).toString());
    }

    private static void unBasicAuth(String token) {
        BasicAuth ba = BasicAuth.fromAuthorizationHeader("BASIC " + token);
        System.err.println("Username=\"" + ba.username + "\", password=\"" + ba.password + "\"");
    }

    private static void addGroup(String groupName) {
        GroupFile f = new GroupFile(new File(dirPath, "group"));
        if (f.addGroup(groupName)) {
            System.err.println("Group \"" + groupName + "\" added");
        }
        else {
            System.err.println("Failed to add group \"" + groupName + "\"");
        }
    }

    private static void rmGroup(String groupName) {
        GroupFile f = new GroupFile(new File(dirPath, "group"));
        if (f.removeGroup(groupName)) {
            System.err.println("Group \"" + groupName + "\" removed");
        }
        else {
            System.err.println("Failed to remove group \"" + groupName + "\"");
        }
    }

    private static void addGroupUser(String groupName, String userName) {
        GroupFile f = new GroupFile(new File(dirPath, "group"));
        if (f.addUserToGroup(userName, groupName)) {
            System.err.println("User \"" + userName + "\" added to group \"" + groupName + "\"");
        }
        else {
            System.err.println("Failed to add user \"" + userName + "\" to group \"" + groupName + "\"");
        }
    }

    private static void rmGroupUser(String groupName, String userName) {
        GroupFile f = new GroupFile(new File(dirPath, "group"));
        if (f.removeUserFromGroup(userName, groupName)) {
            System.err.println("User \"" + userName + "\" removed from group \"" + groupName + "\"");
        }
        else {
            System.err.println("Failed to remove user \"" + userName + "\" from group \"" + groupName + "\"");
        }
    }

    private static void showUser(String userName) {
        PasswordFile fp = new PasswordFile(new File(dirPath, "passwd"));
        User user = fp.getUser(userName);
        if (user == null) {
            System.err.println("User \"" + userName + "\" not found");
            System.exit(0);
        }
        System.err.println("User " + user.userName);
        System.err.println("  Real name      : " + user.longName);
        System.err.println("  Default session: " + user.defaultSession);
        System.err.print  ("  Password       : ");
        ShadowFile fs = new ShadowFile(new File(dirPath, "shadow"));
        if (fs.getPasswordHash(userName) == null) {
            System.err.print("NOT ");
        }
        System.err.println("set");
        GroupFile f = new GroupFile(new File(dirPath, "group"));
        System.err.print  ("  Groups         : ");
        boolean first = true;
        for (String group: f.getGroupsForUser(userName)) {
            if (first) {
                first = false;
            }
            else {
                System.err.print(", ");
            }
            System.err.print(group);
        }
        System.err.println();
    }

    private static void showGroup(String groupName) {
        GroupFile f = new GroupFile(new File(dirPath, "group"));

        if (!f.getGroups().containsKey(groupName)) {
            System.err.println("Group \"" + groupName + "\" not found");
            System.exit(0);
        }
        System.err.println("Group " + groupName);
        System.err.print  ("  Users: ");
        boolean first = true;
        for (String user: f.getUsersInGroup(groupName)) {
            if (first) {
                first = false;
            }
            else {
                System.err.print(", ");
            }
            System.err.print(user);
        }
        System.err.println();
    }

    public static void main(String[] args) {
        try {
            int i=0;
            if (i < args.length) {
                if (args [i].equals("-d") || args [i].equals("--dir")) {
                    i += 1;
                    if (i < args.length) {
                        dirPath = new File(args [i++]);
                    }
                    else {
                        usage();
                        System.exit(-1);
                    }
                }
            }
            if (i < args.length) {
                switch (args[i]) {

                    case "adduser":
                        checkParmCount(i, args.length, 3);
                        addUser(args[i + 1], args[i + 2], args[i + 3]);
                        break;

                    case "rmuser":
                        checkParmCount(i, args.length, 1);
                        rmUser(args[i + 1]);
                        break;

                    case "passwd":
                        checkParmCount(i, args.length, 1);
                        passwd(args[i + 1]);
                        break;

                    case "basicauth":
                        checkParmCount(i, args.length, 1);
                        basicAuth(args[i + 1]);
                        break;

                    case "unbasicauth":
                        checkParmCount(i, args.length, 1);
                        unBasicAuth(args[i + 1]);
                        break;

                    case "addgroup":
                        checkParmCount(i, args.length, 1);
                        addGroup(args[i + 1]);
                        break;

                    case "rmgroup":
                        checkParmCount(i, args.length, 1);
                        rmGroup(args[i + 1]);
                        break;

                    case "addgroupuser":
                        checkParmCount(i, args.length, 2);
                        addGroupUser(args[i + 1], args[i + 2]);
                        break;

                    case "rmgroupuser":
                        checkParmCount(i, args.length, 2);
                        rmGroupUser(args[i + 1], args[i + 2]);
                        break;

                    case "showuser":
                        checkParmCount(i, args.length, 1);
                        showUser(args[i + 1]);
                        break;

                    case "showgroup":
                        checkParmCount(i, args.length, 1);
                        showGroup(args[i + 1]);
                        break;

                    default:
                        throw new Exception("Unknown command \"" + args[i] + "\"");
                }
            }
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            usage();
            System.exit(-1);
        }
    }
}