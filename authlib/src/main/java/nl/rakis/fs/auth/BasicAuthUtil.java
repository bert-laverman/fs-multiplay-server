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
