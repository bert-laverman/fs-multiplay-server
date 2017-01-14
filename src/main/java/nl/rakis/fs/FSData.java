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
package nl.rakis.fs;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Map;

/**
 * Standard stuff everyone should have
 */
public abstract class FSData {

    /**
     * Add field to JsonObjectBuilder if not null, add as null otherwise
     * @param bld
     * @param field
     * @param value
     */
    public static void addIf(JsonObjectBuilder bld, String field, String value)
    {
        if (value == null) {
            bld.addNull(field);
        }
        else {
            bld.add(field, value);
        }
    }

    /**
     * What is the unique key for this object.
     * @return a unique key.
     */
    public abstract String getKey();

    /**
     * Convert this object to a Map
     * @return a HashMap of all fields
     */
    public abstract Map<String,String> toMap();

    /**
     * Convert this object to a JsonObject
     * @return A JsonObject of all fields
     */
    public abstract JsonObject toJsonObject();
}
