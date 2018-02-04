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

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The state of the engine(s).
 */
public class EngineInfo
        extends FSKeylessData
{

    private static final Logger log = Logger.getLogger(EngineInfo.class.getName());

    public static final String TYPE_ENGINES = "Engines";

    private int[] eng;
    private int[] thrt;

    public EngineInfo() {
        super(getType());

        eng = new int[4];
        thrt = new int[4];
    }

    @Override
    public String getKey() {
        return "";
    }

    public static String getType() {
        return TYPE_ENGINES;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String,String> result = new HashMap<>();

        result.put(JsonFields.FIELD_ENGINE_ON, getEng().toString());
        result.put(JsonFields.FIELD_THROTTLE, getEng().toString());

        return result;
    }

    @Override
    public JsonObject toJsonObject() {
        return Json.createObjectBuilder()
                .add(JsonFields.FIELD_ENGINE_ON, toArray(getEng()))
                .add(JsonFields.FIELD_THROTTLE, toArray(getThrt()))
                .build();
    }

    private static int[] copyArray(JsonArray arr, int size) {
        int[] result = null;
        if (arr.size() == size) {
            result = new int[size];
            for (int i = 0; i < size; i++) {
                result[i] = arr.getInt(i);
            }
        }
        return result;
    }

    @Override
    public void updateFromJsonObject(JsonObject obj) {
        if (!obj.isNull(JsonFields.FIELD_ENGINE_ON)) {
            setEng(copyArray(obj.getJsonArray(JsonFields.FIELD_ENGINE_ON), 4));
        }
        if (!obj.isNull(JsonFields.FIELD_THROTTLE)) {
            setThrt(copyArray(obj.getJsonArray(JsonFields.FIELD_THROTTLE), 4));
        }
    }

    public static EngineInfo fromJsonObject(JsonObject obj) {
        EngineInfo result = null;

        if (obj != null) {
            result = new EngineInfo();

            result.setEng(copyArray(obj.getJsonArray(JsonFields.FIELD_ENGINE_ON), 4));
            result.setThrt(copyArray(obj.getJsonArray(JsonFields.FIELD_THROTTLE), 4));
        }

        return result;
    }

    public static EngineInfo fromString(String json) {
        EngineInfo result = null;

        if (json != null) {
            try (StringReader sr = new StringReader(json);
                 JsonReader jr = Json.createReader(sr)) {
                result = fromJsonObject(jr.readObject());
            }
        }
        return result;
    }

    public int[] getEng() {
        return eng;
    }

    public void setEng(int[] eng) {
        this.eng = eng;
    }

    public int[] getThrt() {
        return thrt;
    }

    public void setThrt(int[] thrt) {
        this.thrt = thrt;
    }
}
