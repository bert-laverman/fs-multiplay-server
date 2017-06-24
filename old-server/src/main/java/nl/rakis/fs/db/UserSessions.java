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
package nl.rakis.fs.db;

import nl.rakis.fs.UserSessionInfo;

import javax.annotation.PostConstruct;
import javax.cache.Cache;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.NotAuthorizedException;

/**
 * Keep track of who is logged in.
 */
@ApplicationScoped
public class UserSessions {

    private Cache<String,String> cache;

    @PostConstruct
    private void init() {
        cache = SetupDB.getCacheManager();
    }

    public UserSessionInfo get(String sessionId) {
        if (!cache.containsKey(sessionId)) {
            throw new NotAuthorizedException("Not logged in");
        }
        return UserSessionInfo.fromString(cache.get(sessionId));
    }

    public void put(UserSessionInfo userSession) {
        cache.put(userSession.getKey(), userSession.toString());
    }

    public void remove(String sessionId) {
        cache.remove(sessionId);
    }
}
