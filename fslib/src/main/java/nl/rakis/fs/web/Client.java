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
package nl.rakis.fs.web;

import nl.rakis.fs.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.JsonObject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

public class Client {

    private static final Logger log = LogManager.getLogger(Client.class);

    private String baseUrl;

    public Client(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Client(Config config, String key, String dfl) {
        this.baseUrl = config.get(key, dfl);
    }

    private JsonObject getJsonObjectSynch(String url) {
        if (log.isDebugEnabled()) {
            log.debug("getJsonObjectSynch(\"" + url + "\")");
        }

        javax.ws.rs.client.Client client = null;

        try {
            client = ClientBuilder.newClient();

            return client.target(url).request(MediaType.APPLICATION_JSON).get(JsonObject.class);
        }
        finally {
            if (client != null) {
                client.close();
            }
            if (log.isDebugEnabled()) {
                log.debug("getJsonObjectSynch(): Done");
            }
        }
    }

    public JsonObject getSynch() {
        log.debug("getSynch()");

        return getJsonObjectSynch(baseUrl);
    }

    public JsonObject getSynch(String... args) {
        log.debug("getSynch(...)");

        StringBuilder bld = new StringBuilder(baseUrl);

        for (String arg: args) {
            bld.append('/').append(arg);
        }

        return getJsonObjectSynch(bld.toString());
    }
}
