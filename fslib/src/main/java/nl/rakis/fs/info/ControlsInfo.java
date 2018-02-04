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
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The state of the controls.
 */
public class ControlsInfo
        extends FSKeylessData
{

    private static final Logger log = Logger.getLogger(ControlsInfo.class.getName());

    public static final String TYPE_CONTROLS = "Controls";

    private double rdr;
    private double ele;
    private double ail;
    private double rdrtr;
    private double eletr;
    private double ailtr;
    private double spl;
    private double flp;
    private int grs;
    private double brk;
    private double dr1;

    public ControlsInfo() {
        super(getType());
    }

    @Override
    public String getKey() {
        return "";
    }

    public static String getType() {
        return TYPE_CONTROLS;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String,String> result = new HashMap<>();

        result.put(JsonFields.FIELD_TYPE, getType());
        result.put(JsonFields.FIELD_RUDDER_POS, Double.toString(getRdr()));
        result.put(JsonFields.FIELD_RUDDER_TRIM_POS, Double.toString(getRdrtr()));
        result.put(JsonFields.FIELD_ELEVATOR_POS, Double.toString(getEle()));
        result.put(JsonFields.FIELD_ELEVATOR_TRIM_POS, Double.toString(getEletr()));
        result.put(JsonFields.FIELD_AILERON_POS, Double.toString(getAil()));
        result.put(JsonFields.FIELD_AILERON_TRIM_POS, Double.toString(getAiltr()));
        result.put(JsonFields.FIELD_SPOILERS_POS, Double.toString(getSpl()));
        result.put(JsonFields.FIELD_FLAPS_POS, Double.toString(getFlp()));
        result.put(JsonFields.FIELD_GEARS_DOWN, Integer.toString(getGrs()));
        result.put(JsonFields.FIELD_PARKING_BRAKE_POS, Double.toString(getBrk()));
        result.put(JsonFields.FIELD_DOOR_POS, Double.toString(getDr1()));

        return result;
    }

    @Override
    public JsonObject toJsonObject() {
        return Json.createObjectBuilder()
                .add(JsonFields.FIELD_TYPE, getType())
                .add(JsonFields.FIELD_RUDDER_POS, getRdr())
                .add(JsonFields.FIELD_RUDDER_TRIM_POS, getRdrtr())
                .add(JsonFields.FIELD_ELEVATOR_POS, getEle())
                .add(JsonFields.FIELD_ELEVATOR_TRIM_POS, getEletr())
                .add(JsonFields.FIELD_AILERON_POS, getAil())
                .add(JsonFields.FIELD_AILERON_TRIM_POS, getAiltr())
                .add(JsonFields.FIELD_SPOILERS_POS, getSpl())
                .add(JsonFields.FIELD_FLAPS_POS, getFlp())
                .add(JsonFields.FIELD_GEARS_DOWN, getGrs())
                .add(JsonFields.FIELD_PARKING_BRAKE_POS, getBrk())
                .add(JsonFields.FIELD_DOOR_POS, getDr1())
                .build();
    }

    @Override
    public void updateFromJsonObject(JsonObject obj) {
        if (!obj.isNull(JsonFields.FIELD_RUDDER_POS)) {
            setRdr(obj.getJsonNumber(JsonFields.FIELD_RUDDER_POS).doubleValue());
        }
        if (!obj.isNull(JsonFields.FIELD_RUDDER_TRIM_POS)) {
            setRdrtr(obj.getJsonNumber(JsonFields.FIELD_RUDDER_TRIM_POS).doubleValue());
        }
        if (!obj.isNull(JsonFields.FIELD_ELEVATOR_POS)) {
            setEle(obj.getJsonNumber(JsonFields.FIELD_ELEVATOR_POS).doubleValue());
        }
        if (!obj.isNull(JsonFields.FIELD_ELEVATOR_TRIM_POS)) {
            setEletr(obj.getJsonNumber(JsonFields.FIELD_ELEVATOR_TRIM_POS).doubleValue());
        }
        if (!obj.isNull(JsonFields.FIELD_AILERON_POS)) {
            setAil(obj.getJsonNumber(JsonFields.FIELD_AILERON_POS).doubleValue());
        }
        if (!obj.isNull(JsonFields.FIELD_AILERON_TRIM_POS)) {
            setAiltr(obj.getJsonNumber(JsonFields.FIELD_AILERON_TRIM_POS).doubleValue());
        }
        if (!obj.isNull(JsonFields.FIELD_SPOILERS_POS)) {
            setSpl(obj.getJsonNumber(JsonFields.FIELD_SPOILERS_POS).doubleValue());
        }
        if (!obj.isNull(JsonFields.FIELD_FLAPS_POS)) {
            setFlp(obj.getJsonNumber(JsonFields.FIELD_FLAPS_POS).doubleValue());
        }
        if (!obj.isNull(JsonFields.FIELD_GEARS_DOWN)) {
            setGrs(obj.getInt(JsonFields.FIELD_GEARS_DOWN));
        }
        if (!obj.isNull(JsonFields.FIELD_PARKING_BRAKE_POS)) {
            setBrk(obj.getJsonNumber(JsonFields.FIELD_PARKING_BRAKE_POS).doubleValue());
        }
        if (!obj.isNull(JsonFields.FIELD_DOOR_POS)) {
            setDr1(obj.getJsonNumber(JsonFields.FIELD_DOOR_POS).doubleValue());
        }
    }

    public static ControlsInfo fromJsonObject(JsonObject obj) {
        ControlsInfo result = null;

        if (obj != null) {
            result = new ControlsInfo();

            result.setRdr(obj.getJsonNumber(JsonFields.FIELD_RUDDER_POS).doubleValue());
            result.setRdrtr(obj.getJsonNumber(JsonFields.FIELD_RUDDER_TRIM_POS).doubleValue());
            result.setEle(obj.getJsonNumber(JsonFields.FIELD_ELEVATOR_POS).doubleValue());
            result.setEletr(obj.getJsonNumber(JsonFields.FIELD_ELEVATOR_TRIM_POS).doubleValue());
            result.setAil(obj.getJsonNumber(JsonFields.FIELD_AILERON_POS).doubleValue());
            result.setAiltr(obj.getJsonNumber(JsonFields.FIELD_AILERON_TRIM_POS).doubleValue());
            result.setSpl(obj.getJsonNumber(JsonFields.FIELD_SPOILERS_POS).doubleValue());
            result.setFlp(obj.getJsonNumber(JsonFields.FIELD_FLAPS_POS).doubleValue());
            result.setGrs(obj.getInt(JsonFields.FIELD_GEARS_DOWN));
            result.setBrk(obj.getJsonNumber(JsonFields.FIELD_PARKING_BRAKE_POS).doubleValue());
            result.setDr1(obj.getJsonNumber(JsonFields.FIELD_DOOR_POS).doubleValue());
        }

        return result;
    }

    public static ControlsInfo fromString(String json) {
        ControlsInfo result = null;

        if (json != null) {
            try (StringReader sr = new StringReader(json);
                 JsonReader jr = Json.createReader(sr)) {
                result = fromJsonObject(jr.readObject());
            }
        }
        return result;
    }

    public double getRdr() {
        return rdr;
    }

    public void setRdr(double rdr) {
        this.rdr = rdr;
    }

    public double getEle() {
        return ele;
    }

    public void setEle(double ele) {
        this.ele = ele;
    }

    public double getAil() {
        return ail;
    }

    public void setAil(double ail) {
        this.ail = ail;
    }

    public double getRdrtr() {
        return rdrtr;
    }

    public void setRdrtr(double rdrtr) {
        this.rdrtr = rdrtr;
    }

    public double getEletr() {
        return eletr;
    }

    public void setEletr(double eletr) {
        this.eletr = eletr;
    }

    public double getAiltr() {
        return ailtr;
    }

    public void setAiltr(double ailtr) {
        this.ailtr = ailtr;
    }

    public double getSpl() {
        return spl;
    }

    public void setSpl(double spl) {
        this.spl = spl;
    }

    public double getFlp() {
        return flp;
    }

    public void setFlp(double flp) {
        this.flp = flp;
    }

    public int getGrs() {
        return grs;
    }

    public void setGrs(int grs) {
        this.grs = grs;
    }

    public double getBrk() {
        return brk;
    }

    public void setBrk(double brk) {
        this.brk = brk;
    }

    public double getDr1() {
        return dr1;
    }

    public void setDr1(double dr1) {
        this.dr1 = dr1;
    }
}
