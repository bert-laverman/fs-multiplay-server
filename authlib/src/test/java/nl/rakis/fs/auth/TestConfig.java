package nl.rakis.fs.auth;

import nl.rakis.fs.config.Config;

import javax.enterprise.inject.Alternative;

@Alternative
public class TestConfig
    extends Config
{
    public TestConfig() {
        super();
        put(TokenManager.CFG_AUTH_CERTDIR, "build");
    }
}
