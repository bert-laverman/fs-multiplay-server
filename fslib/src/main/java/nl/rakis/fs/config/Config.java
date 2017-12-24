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
package nl.rakis.fs.config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * This should go as soon as I get a valid alternative set up, like Microprofile Config
 */
@Singleton
public class Config
{

    private Map<String,String> cachedValues = new HashMap<>();

    /**
     * Get the cached value if available, otherwise an environment variable.
     * @param key The name of the value.
     * @return The cached value if available, otherwise the environment variable's value.
     */
    public String get(final String key) {
        return cachedValues.containsKey(key) ? cachedValues.get(key) : System.getenv(key);
    }

    /**
     * Cache a value, so it overrides an environment variable. If the value is null, remove any cached value.
     * @param key The name of the value.
     * @param value The value to store.
     */
    public void put(String key, String value) {
        if (value == null) {
            cachedValues.remove(key);
        }
        else {
            cachedValues.put(key, value);
        }
    }

    /**
     * Get a value, or the given default if none available.
     * @param key The name of the value.
     * @param def The default value.
     * @return The cached value if available, otherwise the environment variable's value if available, otherwise the default.
     */
    public String get(final String key, final String def) {
        final String result = get(key);

        return (result == null) ? def : result;
    }
}
