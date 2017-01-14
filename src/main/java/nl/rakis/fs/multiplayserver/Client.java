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
package nl.rakis.fs.multiplayserver;

import java.util.logging.Logger;

/**
 * A connected FS player.
 */
public class Client {

    private static final Logger log = Logger.getLogger(Client.class.getName());

    private String session;
    private String callsign;
    private String key;
    private String sessionId;

    public Client(String session, String callsign) {
        this.session = session;
        this.callsign = callsign;
        this.key = session + ":" + callsign;
    }

    public Client(String session, String callsign, String sessionId) {
        this.session = session;
        this.callsign = callsign;
        this.key = session + ":" + callsign;
        this.sessionId = sessionId;
    }

    public String getSession() {
        return session;
    }

    public String getCallsign() {
        return callsign;
    }

    public String getKey() {
        return key;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
