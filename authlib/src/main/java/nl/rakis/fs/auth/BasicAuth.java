package nl.rakis.fs.auth;

/**
 * Basic Authentication
 */
public class BasicAuth {
    public String username;
    public String password;

    public BasicAuth() {
    }

    public BasicAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
