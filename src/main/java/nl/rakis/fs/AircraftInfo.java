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

    public static final String FIELD_TYPE = "type";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_CALLSIGN = "atcId";
    public static final String FIELD_ATC_MODEL = "atcModel";
    public static final String FIELD_ATC_TYPE = "atcType";
    public static final String FIELD_ATC_AIRLINE = "atcAirline";
    public static final String FIELD_ATC_FLNUM = "atcFlightNumber";

    private String username;
    private String title;
    private String atcId;
    private String atcModel;
    private String atcType;
    private String atcAirline;
    private String atcFlightNumber;

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

        result.put(FIELD_TYPE, getType());
        result.put(FIELD_USERNAME, (username == null) ? "" : username);
        result.put(FIELD_TITLE, (title == null) ? "" : title);
        result.put(FIELD_CALLSIGN, (atcId == null) ? "" : atcId);
        result.put(FIELD_ATC_TYPE, (atcType == null) ? "" : atcType);
        result.put(FIELD_ATC_MODEL, (atcModel == null) ? "" : atcModel);
        result.put(FIELD_ATC_AIRLINE, (atcAirline == null) ? "" : atcAirline);
        result.put(FIELD_ATC_FLNUM, (atcFlightNumber == null) ? "" : atcFlightNumber);

        return result;
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObjectBuilder bld = Json.createObjectBuilder()
                .add(FIELD_TYPE, getType())
                .add(FIELD_CALLSIGN, getAtcId());

        addIf(bld, FIELD_USERNAME, getUsername());
        addIf(bld, FIELD_TITLE, getTitle());
        addIf(bld, FIELD_ATC_TYPE, getAtcType());
        addIf(bld, FIELD_ATC_MODEL, getAtcModel());
        addIf(bld, FIELD_ATC_AIRLINE, getAtcAirline());
        addIf(bld, FIELD_ATC_FLNUM, getAtcFlightNumber());

        return bld.build();
    }

    @Override
    public String toString() {
        return toJsonObject().toString();
    }

    public static AircraftInfo fromJsonObject(JsonObject obj) {
        AircraftInfo result = null;

        if (obj != null) {
            result = new AircraftInfo(obj.getString(FIELD_CALLSIGN));
            result.setTitle(obj.getString(FIELD_TITLE));
            if (!obj.isNull(FIELD_USERNAME)) {
                result.setUsername(obj.getString(FIELD_USERNAME));
            }
            if (!obj.isNull(FIELD_ATC_TYPE)) {
                result.setAtcType(obj.getString(FIELD_ATC_TYPE));
            }
            if (!obj.isNull(FIELD_ATC_MODEL)) {
                result.setAtcModel(obj.getString(FIELD_ATC_MODEL));
            }
            if (!obj.isNull(FIELD_ATC_AIRLINE)) {
                result.setAtcAirline(obj.getString(FIELD_ATC_AIRLINE));
            }
            if (!obj.isNull(FIELD_ATC_FLNUM)) {
                result.setAtcFlightNumber(obj.getString(FIELD_ATC_FLNUM));
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
}
