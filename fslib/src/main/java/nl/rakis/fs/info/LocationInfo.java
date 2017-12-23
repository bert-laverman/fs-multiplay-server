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
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class LocationInfo
    extends FSKeylessData
{

    private static final Logger log = Logger.getLogger(LocationInfo.class.getName());

    public static final String LOCATION_TYPE = "Location";

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
        super(getType());
    }

    public static String getType() {
        return LOCATION_TYPE;
    }

    @Override
    public String getKey() {
        return getType() + ":" + getLatitude() + ":" + getLongitude();
    }

    @Override
    public Map<String, String> toMap() {
        Map<String,String> result = new HashMap<>();

        result.put(JsonFields.FIELD_TYPE, getType());
        result.put(JsonFields.FIELD_CALLSIGN, getCallsign());
        result.put(JsonFields.FIELD_LATITUDE, getLatitude());
        result.put(JsonFields.FIELD_LONGITUDE, getLongitude());
        result.put(JsonFields.FIELD_ALTITUDE, getAltitude());
        result.put(JsonFields.FIELD_PITCH, getPitch());
        result.put(JsonFields.FIELD_BANK, getBank());
        result.put(JsonFields.FIELD_HEADING, getHeading());
        result.put(JsonFields.FIELD_ON_GROUND, Boolean.toString(isOnGround()));
        result.put(JsonFields.FIELD_AIRSPEED, getAirspeed());

        return result;
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObjectBuilder bld = Json.createObjectBuilder()
                .add(JsonFields.FIELD_TYPE, getType())
                .add(JsonFields.FIELD_LATITUDE, getLatitude())
                .add(JsonFields.FIELD_LONGITUDE, getLongitude())
                .add(JsonFields.FIELD_ALTITUDE, getAltitude())
                .add(JsonFields.FIELD_PITCH, getPitch())
                .add(JsonFields.FIELD_BANK, getBank())
                .add(JsonFields.FIELD_HEADING, getHeading())
                .add(JsonFields.FIELD_ON_GROUND, isOnGround())
                .add(JsonFields.FIELD_AIRSPEED, getAirspeed());
        if (getCallsign() != null) {
            bld.add(JsonFields.FIELD_CALLSIGN, getCallsign());
        }
        return bld.build();
    }

    @Override
    public String toString() {
        return toJsonObject().toString();
    }

    @Override
    public void updateFromJsonObject(JsonObject obj) {
        if (!obj.isNull(JsonFields.FIELD_CALLSIGN)) {
            setCallsign(obj.getString(JsonFields.FIELD_CALLSIGN));
        }
        if (obj.containsKey(JsonFields.FIELD_LATITUDE) && !obj.isNull(JsonFields.FIELD_LATITUDE)) {
            setLatitude(obj.getString(JsonFields.FIELD_LATITUDE));
        }
        if (obj.containsKey(JsonFields.FIELD_LONGITUDE) && !obj.isNull(JsonFields.FIELD_LONGITUDE)) {
            setLongitude(obj.getString(JsonFields.FIELD_LONGITUDE));
        }
        if (obj.containsKey(JsonFields.FIELD_ALTITUDE) && !obj.isNull(JsonFields.FIELD_ALTITUDE)) {
            setAltitude(obj.getString(JsonFields.FIELD_ALTITUDE));
        }
        if (obj.containsKey(JsonFields.FIELD_PITCH) && !obj.isNull(JsonFields.FIELD_PITCH)) {
            setPitch(obj.getString(JsonFields.FIELD_PITCH));
        }
        if (obj.containsKey(JsonFields.FIELD_BANK) && !obj.isNull(JsonFields.FIELD_BANK)) {
            setBank(obj.getString(JsonFields.FIELD_BANK));
        }
        if (obj.containsKey(JsonFields.FIELD_HEADING) && !obj.isNull(JsonFields.FIELD_HEADING)) {
            setHeading(obj.getString(JsonFields.FIELD_HEADING));
        }
        if (obj.containsKey(JsonFields.FIELD_ON_GROUND) && !obj.isNull(JsonFields.FIELD_ON_GROUND)) {
            setOnGround(obj.getBoolean(JsonFields.FIELD_ON_GROUND));
        }
        if (obj.containsKey(JsonFields.FIELD_AIRSPEED) && !obj.isNull(JsonFields.FIELD_AIRSPEED)) {
            setAirspeed(obj.getString(JsonFields.FIELD_AIRSPEED));
        }
    }

    public static LocationInfo fromJsonObject(JsonObject obj) {
        LocationInfo result = null;

        if (obj != null) {
            result = new LocationInfo();
            if (!obj.isNull(JsonFields.FIELD_CALLSIGN)) {
                result.setCallsign(obj.getString(JsonFields.FIELD_CALLSIGN));
            }
            if (obj.containsKey(JsonFields.FIELD_LATITUDE) && !obj.isNull(JsonFields.FIELD_LATITUDE)) {
                result.setLatitude(obj.getString(JsonFields.FIELD_LATITUDE));
            }
            if (obj.containsKey(JsonFields.FIELD_LONGITUDE) && !obj.isNull(JsonFields.FIELD_LONGITUDE)) {
                result.setLongitude(obj.getString(JsonFields.FIELD_LONGITUDE));
            }
            if (obj.containsKey(JsonFields.FIELD_ALTITUDE) && !obj.isNull(JsonFields.FIELD_ALTITUDE)) {
                result.setAltitude(obj.getString(JsonFields.FIELD_ALTITUDE));
            }
            if (obj.containsKey(JsonFields.FIELD_PITCH) && !obj.isNull(JsonFields.FIELD_PITCH)) {
                result.setPitch(obj.getString(JsonFields.FIELD_PITCH));
            }
            if (obj.containsKey(JsonFields.FIELD_BANK) && !obj.isNull(JsonFields.FIELD_BANK)) {
                result.setBank(obj.getString(JsonFields.FIELD_BANK));
            }
            if (obj.containsKey(JsonFields.FIELD_HEADING) && !obj.isNull(JsonFields.FIELD_HEADING)) {
                result.setHeading(obj.getString(JsonFields.FIELD_HEADING));
            }
            if (obj.containsKey(JsonFields.FIELD_ON_GROUND) && !obj.isNull(JsonFields.FIELD_ON_GROUND)) {
                result.setOnGround(obj.getBoolean(JsonFields.FIELD_ON_GROUND));
            }
            if (obj.containsKey(JsonFields.FIELD_AIRSPEED) && !obj.isNull(JsonFields.FIELD_AIRSPEED)) {
                result.setAirspeed(obj.getString(JsonFields.FIELD_AIRSPEED));
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

    public void parse(String json) {
        log.finest("parse(): \"" + json + "\"");

        if (json != null) {
            try (StringReader sr = new StringReader(json);
                 JsonReader jr = Json.createReader(sr)) {
                final JsonObject obj = jr.readObject();
                log.finest("parse(): JsonReader.readObject() returned \"" + obj.toString() + "\"");

                if (obj.containsKey(JsonFields.FIELD_CALLSIGN) && !obj.isNull(JsonFields.FIELD_CALLSIGN)) {
                    setCallsign(obj.getString(JsonFields.FIELD_CALLSIGN));
                }
                if (obj.containsKey(JsonFields.FIELD_LATITUDE) && !obj.isNull(JsonFields.FIELD_LATITUDE)) {
                    setLatitude(obj.getString(JsonFields.FIELD_LATITUDE));
                }
                if (obj.containsKey(JsonFields.FIELD_LONGITUDE) && !obj.isNull(JsonFields.FIELD_LONGITUDE)) {
                    setLongitude(obj.getString(JsonFields.FIELD_LONGITUDE));
                }
                if (obj.containsKey(JsonFields.FIELD_ALTITUDE) && !obj.isNull(JsonFields.FIELD_ALTITUDE)) {
                    setAltitude(obj.getString(JsonFields.FIELD_ALTITUDE));
                }
                if (obj.containsKey(JsonFields.FIELD_PITCH) && !obj.isNull(JsonFields.FIELD_PITCH)) {
                    setPitch(obj.getString(JsonFields.FIELD_PITCH));
                }
                if (obj.containsKey(JsonFields.FIELD_BANK) && !obj.isNull(JsonFields.FIELD_BANK)) {
                    setBank(obj.getString(JsonFields.FIELD_BANK));
                }
                if (obj.containsKey(JsonFields.FIELD_HEADING) && !obj.isNull(JsonFields.FIELD_HEADING)) {
                    setHeading(obj.getString(JsonFields.FIELD_HEADING));
                }
                if (obj.containsKey(JsonFields.FIELD_ON_GROUND) && !obj.isNull(JsonFields.FIELD_ON_GROUND)) {
                    setOnGround(obj.getBoolean(JsonFields.FIELD_ON_GROUND));
                }
                if (obj.containsKey(JsonFields.FIELD_AIRSPEED) && !obj.isNull(JsonFields.FIELD_AIRSPEED)) {
                    setAirspeed(obj.getString(JsonFields.FIELD_AIRSPEED));
                }
            }
        }
        log.finest("parse(): result is \"" + toString() + "\"");
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
