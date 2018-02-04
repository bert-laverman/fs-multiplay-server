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
package nl.rakis.fs.info;

import javax.json.*;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Standard stuff everyone should have
 */
public abstract class FSData {

    private static final Logger log = Logger.getLogger(FSData.class.getName());

    private String href;

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
     * Add field to JsonObjectBuilder if not null, add as null otherwise
     * @param bld
     * @param field
     * @param value
     */
    public static void addIf(JsonObjectBuilder bld, String field, FSData value)
    {
        if (value == null) {
            bld.addNull(field);
        }
        else {
            bld.add(field, value.toJsonObject());
        }
    }

    public static boolean getBoolIf(JsonObject obj, String property, boolean deflt)
    {
        boolean result = deflt;

        if ((obj != null) && (property != null) && obj.containsKey(property) && !obj.isNull(property)) {
            try {
                result = obj.getBoolean(property);
            } catch (ClassCastException e) {
                try {
                    result = Boolean.parseBoolean(obj.getString(property));
                } catch (ClassCastException e1) {
                    log.warning("Ignoring property \"" + property + "\", cannot turn it into a boolean");
                }
            }
        }
        return result;
    }

    public static JsonArray toArray(int[] arr) {
        JsonArrayBuilder bld = Json.createArrayBuilder();

        if (arr != null) {
            for (int i : arr) {
                bld.add(i);
            }
        }
        return bld.build();
    }

    public static JsonArray toArray(List<String> list) {
        JsonArrayBuilder bld = Json.createArrayBuilder();

        if (list != null) {
            for (String s : list) {
                bld.add(s);
            }
        }
        return bld.build();
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

    /**
     * Update fields if passed, ignore unknown fields, leave missing fields alone.
     * @param obj The JsonObject to obtain updates from
     */
    public abstract void updateFromJsonObject(JsonObject obj);

    /**
     * Convert to serialized JSON.
     * @return A String containing the serialized object.
     */
    @Override
    public String toString() {
        return toJsonObject().toString();
    }

    /**
     * Parse a serialized JSON object and initialize the object from it.
     * @param jsonString the serialized JSON object
     */
    public void parse(String jsonString) {
        log.finest("parse(): \"" + jsonString + "\"");

        if (jsonString != null) {
            try (StringReader sr = new StringReader(jsonString);
                 JsonReader jr = Json.createReader(sr)) {
                final JsonObject obj = jr.readObject();
                log.finest("parse(): JsonReader.readObject() returned \"" + obj.toString() + "\"");

                updateFromJsonObject(obj);
            }
        }
        log.finest("parse(): result is \"" + toString() + "\"");

    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
