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
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class LocationInfo
    extends FSData
{
    public static final String LOCATION_TYPE = "Location";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_CALLSIGN = "callsign";
    public static final String FIELD_LATITUDE = "latitude";
    public static final String FIELD_LONGITUDE = "longitude";
    public static final String FIELD_ALTITUDE = "altitude";
    public static final String FIELD_PITCH = "pitch";
    public static final String FIELD_BANK = "bank";
    public static final String FIELD_HEADING = "heading";
    public static final String FIELD_ON_GROUND = "onGround";
    public static final String FIELD_AIRSPEED = "airspeed";

    private String callsign;
    private String latitude;
    private String longitude;
    private String altitude;
    private String pitch;
    private String bank;
    private String heading;
    private boolean onGround;
    private String airspeed;

    public LocationInfo() {
    }

    public static String getType() {
        return LOCATION_TYPE;
    }

    @Override
    public String getKey() {
        return getType() + ":" + getLatitude() + ":" + getLongitude();
    }

    public String getKey(String session, String callsign) {
        return getType() + ":" + session + ":" + callsign;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String,String> result = new HashMap<>();

        result.put(FIELD_TYPE, getType());
        result.put(FIELD_CALLSIGN, getCallsign());
        result.put(FIELD_LATITUDE, getLatitude());
        result.put(FIELD_LONGITUDE, getLongitude());
        result.put(FIELD_ALTITUDE, getAltitude());
        result.put(FIELD_PITCH, getPitch());
        result.put(FIELD_BANK, getBank());
        result.put(FIELD_HEADING, getHeading());
        result.put(FIELD_ON_GROUND, Boolean.toString(isOnGround()));
        result.put(FIELD_AIRSPEED, getAirspeed());

        return result;
    }

    @Override
    public JsonObject toJsonObject() {
        return Json.createObjectBuilder()
                .add(FIELD_TYPE, getType())
                .add(FIELD_CALLSIGN, getCallsign())
                .add(FIELD_LATITUDE, getLatitude())
                .add(FIELD_LONGITUDE, getLongitude())
                .add(FIELD_ALTITUDE, getAltitude())
                .add(FIELD_PITCH, getPitch())
                .add(FIELD_BANK, getBank())
                .add(FIELD_HEADING, getHeading())
                .add(FIELD_ON_GROUND, isOnGround())
                .add(FIELD_AIRSPEED, getAirspeed())
                .build();
    }

    @Override
    public String toString() {
        return toJsonObject().toString();
    }

    public static LocationInfo fromJsonObject(JsonObject obj) {
        LocationInfo result = null;

        if (obj != null) {
            result = new LocationInfo();
            if (!obj.isNull(FIELD_CALLSIGN)) {
                result.setCallsign(obj.getString(FIELD_CALLSIGN));
            }
            if (obj.containsKey(FIELD_LATITUDE) && !obj.isNull(FIELD_LATITUDE)) {
                result.setLatitude(obj.getString(FIELD_LATITUDE));
            }
            if (obj.containsKey(FIELD_LONGITUDE) && !obj.isNull(FIELD_LONGITUDE)) {
                result.setLongitude(obj.getString(FIELD_LONGITUDE));
            }
            if (obj.containsKey(FIELD_ALTITUDE) && !obj.isNull(FIELD_ALTITUDE)) {
                result.setAltitude(obj.getString(FIELD_ALTITUDE));
            }
            if (obj.containsKey(FIELD_PITCH) && !obj.isNull(FIELD_PITCH)) {
                result.setPitch(obj.getString(FIELD_PITCH));
            }
            if (obj.containsKey(FIELD_BANK) && !obj.isNull(FIELD_BANK)) {
                result.setBank(obj.getString(FIELD_BANK));
            }
            if (obj.containsKey(FIELD_HEADING) && !obj.isNull(FIELD_HEADING)) {
                result.setHeading(obj.getString(FIELD_HEADING));
            }
            if (obj.containsKey(FIELD_ON_GROUND) && !obj.isNull(FIELD_ON_GROUND)) {
                result.setOnGround(obj.getBoolean(FIELD_ON_GROUND));
            }
            if (obj.containsKey(FIELD_AIRSPEED) && !obj.isNull(FIELD_AIRSPEED)) {
                result.setAirspeed(obj.getString(FIELD_AIRSPEED));
            }
        }

        return result;
    }

    public static LocationInfo fromString(String json) {
        LocationInfo result = null;

        if (json != null) {
            try (StringReader sr = new StringReader(json);
                 JsonReader jr = Json.createReader(sr)) {
                result = fromJsonObject(jr.readObject());
            }
        }
        return result;
    }

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getPitch() {
        return pitch;
    }

    public void setPitch(String pitch) {
        this.pitch = pitch;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public String getAirspeed() {
        return airspeed;
    }

    public void setAirspeed(String airspeed) {
        this.airspeed = airspeed;
    }
}
