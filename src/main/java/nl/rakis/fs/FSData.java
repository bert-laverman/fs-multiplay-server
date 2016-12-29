/*
 * Copyright 2016 Bert Laverman
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
import java.util.Map;

/**
 * Standard stuff everyone should have
 */
public interface FSData {

    /**
     * What is the unique key for this object.
     * @return a unique key.
     */
    String getKey();

    /**
     * Convert this object to a Map
     * @return a HashMap of all fields
     */
    Map<String,String> toMap();

    /**
     * Convert this object to a JsonObject
     * @return A JsonObject of all fields
     */
    JsonObject toJsonObject();
}
