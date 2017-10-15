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

import javax.ws.rs.BadRequestException;
import java.util.Base64;

/**
 * Code to deal with Basic Authentication
 */
public class BasicAuthUtil {

    public static BasicAuth decodeAuthorizationHeader(String token)
    {
        String[] headerFields = token.split(" ");
        if ((headerFields.length != 2)
                || (headerFields [0] == null) || !headerFields [0].equalsIgnoreCase("basic")
                || (headerFields [1] == null))
        {
            throw new BadRequestException("Bad request");
        }
        String decodedToken = new String(Base64.getDecoder().decode(headerFields [1]));
        String[] tokenFields = decodedToken.split(":");
        if ((tokenFields.length != 2) || (tokenFields [0] == null) || (tokenFields [1] == null)) {
            throw new BadRequestException("Bad request");
        }
        BasicAuth result = new BasicAuth(tokenFields [0], tokenFields [1]);

        return result;
    }
}
