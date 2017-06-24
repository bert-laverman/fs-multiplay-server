/*
 * Copyright 2016, 2017 Bert Laverman
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

import javax.enterprise.context.RequestScoped;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * The REST API for Authentication
 */
@Path("/")
@RequestScoped
public class Authenticate {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkMe(JsonObject obj, @QueryParam("redirect_to") String redir) {
        URI uri;
        try {
            uri = new URI(redir);
        } catch (URISyntaxException e) {
            throw new BadRequestException("Bad redirect URI");
        }
        Response bld = Response.temporaryRedirect(uri).build();

        return bld;
    }
}
