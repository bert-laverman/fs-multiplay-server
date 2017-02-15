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

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Aircraft.
 */
public class AircraftInfo
    extends FSData
{
    public static final String AIRCRAFT_TYPE = "Aircraft";

    private String username;
    private String title;
    private String atcId;
    private String atcModel;
    private String atcType;
    private String atcAirline;
    private String atcFlightNumber;

    private LocationInfo location;
    private EngineInfo   engines;
    private LightInfo    lights;
    private ControlsInfo controls;

    public AircraftInfo() {
    }

    public AircraftInfo(String callsign) {
        this.atcId = callsign;
    }

    public static String getType() {
        return AIRCRAFT_TYPE;
    }

    @Override
    public String getKey() {
        return getType() + ":" + getAtcId();
    }

    public String getKey(String session) {
        return getType() + ":" + session + ":" + getAtcId();
    }

    @Override
    public Map<String, String> toMap() {
        Map<String,String> result = new HashMap<>();

        result.put(JsonFields.FIELD_TYPE, getType());
        result.put(JsonFields.FIELD_USERNAME, (username == null) ? "" : username);
        result.put(JsonFields.FIELD_TITLE, (title == null) ? "" : title);
        result.put(JsonFields.FIELD_ATC_ID, (atcId == null) ? "" : atcId);
        result.put(JsonFields.FIELD_ATC_TYPE, (atcType == null) ? "" : atcType);
        result.put(JsonFields.FIELD_ATC_MODEL, (atcModel == null) ? "" : atcModel);
        result.put(JsonFields.FIELD_ATC_AIRLINE, (atcAirline == null) ? "" : atcAirline);
        result.put(JsonFields.FIELD_ATC_FLNUM, (atcFlightNumber == null) ? "" : atcFlightNumber);

        return result;
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObjectBuilder bld = Json.createObjectBuilder()
                .add(JsonFields.FIELD_TYPE, getType())
                .add(JsonFields.FIELD_ATC_ID, getAtcId());

        addIf(bld, JsonFields.FIELD_USERNAME, getUsername());
        addIf(bld, JsonFields.FIELD_TITLE, getTitle());
        addIf(bld, JsonFields.FIELD_ATC_TYPE, getAtcType());
        addIf(bld, JsonFields.FIELD_ATC_MODEL, getAtcModel());
        addIf(bld, JsonFields.FIELD_ATC_AIRLINE, getAtcAirline());
        addIf(bld, JsonFields.FIELD_ATC_FLNUM, getAtcFlightNumber());

        return bld.build();
    }

    @Override
    public String toString() {
        return toJsonObject().toString();
    }

    public static AircraftInfo fromJsonObject(JsonObject obj) {
        AircraftInfo result = null;

        if (obj != null) {
            result = new AircraftInfo(obj.getString(JsonFields.FIELD_ATC_ID));
            result.setTitle(obj.getString(JsonFields.FIELD_TITLE));
            if (!obj.isNull(JsonFields.FIELD_USERNAME)) {
                result.setUsername(obj.getString(JsonFields.FIELD_USERNAME));
            }
            if (!obj.isNull(JsonFields.FIELD_ATC_TYPE)) {
                result.setAtcType(obj.getString(JsonFields.FIELD_ATC_TYPE));
            }
            if (!obj.isNull(JsonFields.FIELD_ATC_MODEL)) {
                result.setAtcModel(obj.getString(JsonFields.FIELD_ATC_MODEL));
            }
            if (!obj.isNull(JsonFields.FIELD_ATC_AIRLINE)) {
                result.setAtcAirline(obj.getString(JsonFields.FIELD_ATC_AIRLINE));
            }
            if (!obj.isNull(JsonFields.FIELD_ATC_FLNUM)) {
                result.setAtcFlightNumber(obj.getString(JsonFields.FIELD_ATC_FLNUM));
            }
        }

        return result;
    }

    public static AircraftInfo fromString(String json) {
        AircraftInfo result = null;

        if (json != null) {
            try (StringReader sr = new StringReader(json);
                 JsonReader jr = Json.createReader(sr)) {
                result = fromJsonObject(jr.readObject());
            }
        }
        return result;
    }

    public void parse(String json) {
        if (json != null) {
            try (StringReader sr = new StringReader(json);
                 JsonReader jr = Json.createReader(sr)) {
                final JsonObject obj = jr.readObject();

                setAtcId(obj.getString(JsonFields.FIELD_ATC_ID));
                setTitle(obj.getString(JsonFields.FIELD_TITLE));
                if (!obj.isNull(JsonFields.FIELD_USERNAME)) {
                    setUsername(obj.getString(JsonFields.FIELD_USERNAME));
                }
                if (!obj.isNull(JsonFields.FIELD_ATC_TYPE)) {
                    setAtcType(obj.getString(JsonFields.FIELD_ATC_TYPE));
                }
                if (!obj.isNull(JsonFields.FIELD_ATC_MODEL)) {
                    setAtcModel(obj.getString(JsonFields.FIELD_ATC_MODEL));
                }
                if (!obj.isNull(JsonFields.FIELD_ATC_AIRLINE)) {
                    setAtcAirline(obj.getString(JsonFields.FIELD_ATC_AIRLINE));
                }
                if (!obj.isNull(JsonFields.FIELD_ATC_FLNUM)) {
                    setAtcFlightNumber(obj.getString(JsonFields.FIELD_ATC_FLNUM));
                }
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAtcId() {
        return atcId;
    }

    public void setAtcId(String atcId) {
        this.atcId = atcId;
    }

    public String getAtcModel() {
        return atcModel;
    }

    public void setAtcModel(String atcModel) {
        this.atcModel = atcModel;
    }

    public String getAtcType() {
        return atcType;
    }

    public void setAtcType(String atcType) {
        this.atcType = atcType;
    }

    public String getAtcAirline() {
        return atcAirline;
    }

    public void setAtcAirline(String atcAirline) {
        this.atcAirline = atcAirline;
    }

    public String getAtcFlightNumber() {
        return atcFlightNumber;
    }

    public void setAtcFlightNumber(String atcFlightNumber) {
        this.atcFlightNumber = atcFlightNumber;
    }

    public LocationInfo getLocation() {
        return location;
    }

    public void setLocation(LocationInfo location) {
        this.location = location;
    }

    public EngineInfo getEngines() {
        return engines;
    }

    public void setEngines(EngineInfo engines) {
        this.engines = engines;
    }

    public LightInfo getLights() {
        return lights;
    }

    public void setLights(LightInfo lights) {
        this.lights = lights;
    }

    public ControlsInfo getControls() {
        return controls;
    }

    public void setControls(ControlsInfo controls) {
        this.controls = controls;
    }
}
