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

/**
 * This should go as soon as I get a valid alternative set up, like Microprofile Config
 */
public class Config
{

    public String get(final String key) {
        return System.getenv(key);
    }

    public String get(final String key, final String def) {
        final String result = get(key);
        return (result == null) ? def : result;
    }
}
