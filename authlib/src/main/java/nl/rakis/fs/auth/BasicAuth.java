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

import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;

/**
 * Basic Authentication
 */
public class BasicAuth {

    private static final Logger log = Logger.getLogger(BasicAuth.class.getName());

    public String username;
    public String password;

    public BasicAuth() {
    }

    public BasicAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static final String BASIC_PREFIX = "BASIC ";
    public static final int BP_LEN = BASIC_PREFIX.length();

    public static BasicAuth fromAuthorizationHeader(final String token)
    {
        if (log.isLoggable(Level.FINER)) {
            log.finer("fromAuthorizationHeader(\"" + token + "\")");
        }

        if ((token.length() < BP_LEN) || !token.substring(0, BP_LEN).equalsIgnoreCase(BASIC_PREFIX)) {
            log.warning("fromAuthorizationHeader(): Bad prefix");
            throw new BadRequestException("Bad request");
        }
        String decodedToken = new String(Base64.getDecoder().decode(token.substring(BP_LEN)));

        String[] tokenFields = decodedToken.split(":");
        if ((tokenFields.length != 2) || (tokenFields [0] == null) || (tokenFields [1] == null)) {
            log.warning("fromAuthorizationHeader(): Bad decodedToken \"" + decodedToken + "\"");
            throw new BadRequestException("Bad request");
        }
        BasicAuth result = new BasicAuth(tokenFields [0], tokenFields [1]);

        if (log.isLoggable(Level.FINER)) {
            log.finer("fromAuthorizationHeader(): username = \"" + result.username + "\"");
        }
        return result;
    }

    @Override
    public String toString() {
        final String token = username + ":" + password;
        return BASIC_PREFIX + Base64.getEncoder().encodeToString(token.getBytes());
    }
}
