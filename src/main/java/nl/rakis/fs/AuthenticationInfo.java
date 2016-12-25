package nl.rakis.fs;

/**
 * Created by bertl on 12/18/2016.
 */
public class AuthenticationInfo {
    private String username;
    private String password;
    private String session;

    public AuthenticationInfo() {

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
