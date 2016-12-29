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
    private double latitude;
    private double longitude;
    private double altitude;
    private double pitch;
    private double bank;
    private double heading;
    private boolean onGround;
    private long airspeed;

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
        result.put(FIELD_LATITUDE, Double.toString(getLatitude()));
        result.put(FIELD_LONGITUDE, Double.toString(getLongitude()));
        result.put(FIELD_ALTITUDE, Double.toString(getAltitude()));
        result.put(FIELD_PITCH, Double.toString(getPitch()));
        result.put(FIELD_BANK, Double.toString(getBank()));
        result.put(FIELD_HEADING, Double.toString(getHeading()));
        result.put(FIELD_ON_GROUND, Boolean.toString(isOnGround()));
        result.put(FIELD_AIRSPEED, Long.toString(getAirspeed()));

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
                result.setLatitude(obj.getJsonNumber(FIELD_LATITUDE).doubleValue());
            }
            if (obj.containsKey(FIELD_LONGITUDE) && !obj.isNull(FIELD_LONGITUDE)) {
                result.setLongitude(obj.getJsonNumber(FIELD_LONGITUDE).doubleValue());
            }
            if (obj.containsKey(FIELD_ALTITUDE) && !obj.isNull(FIELD_ALTITUDE)) {
                result.setAltitude(obj.getJsonNumber(FIELD_ALTITUDE).doubleValue());
            }
            if (obj.containsKey(FIELD_PITCH) && !obj.isNull(FIELD_PITCH)) {
                result.setPitch(obj.getJsonNumber(FIELD_PITCH).doubleValue());
            }
            if (obj.containsKey(FIELD_BANK) && !obj.isNull(FIELD_BANK)) {
                result.setBank(obj.getJsonNumber(FIELD_BANK).doubleValue());
            }
            if (obj.containsKey(FIELD_HEADING) && !obj.isNull(FIELD_HEADING)) {
                result.setHeading(obj.getJsonNumber(FIELD_HEADING).doubleValue());
            }
            if (obj.containsKey(FIELD_ON_GROUND) && !obj.isNull(FIELD_ON_GROUND)) {
                result.setOnGround(obj.getBoolean(FIELD_ON_GROUND));
            }
            if (obj.containsKey(FIELD_AIRSPEED) && !obj.isNull(FIELD_AIRSPEED)) {
                result.setAirspeed(obj.getJsonNumber(FIELD_AIRSPEED).longValue());
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public double getBank() {
        return bank;
    }

    public void setBank(double bank) {
        this.bank = bank;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public long getAirspeed() {
        return airspeed;
    }

    public void setAirspeed(long airspeed) {
        this.airspeed = airspeed;
    }
}
