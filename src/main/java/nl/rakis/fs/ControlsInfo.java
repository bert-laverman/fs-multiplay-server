package nl.rakis.fs;

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

    private int rdr;
    private int ele;
    private int ail;
    private double rdrtr;
    private double eletr;
    private double ailtr;
    private int spl;
    private double flp;
    private int grs;
    private int brk;
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
        result.put(JsonFields.FIELD_RUDDER_POS, Integer.toString(getRdr()));
        result.put(JsonFields.FIELD_RUDDER_TRIM_POS, Double.toString(getRdrtr()));
        result.put(JsonFields.FIELD_ELEVATOR_POS, Integer.toString(getEle()));
        result.put(JsonFields.FIELD_ELEVATOR_TRIM_POS, Double.toString(getEletr()));
        result.put(JsonFields.FIELD_AILERON_POS, Integer.toString(getAil()));
        result.put(JsonFields.FIELD_AILERON_TRIM_POS, Double.toString(getAiltr()));
        result.put(JsonFields.FIELD_SPOILERS_POS, Integer.toString(getSpl()));
        result.put(JsonFields.FIELD_FLAPS_POS, Double.toString(getFlp()));
        result.put(JsonFields.FIELD_GEARS_DOWN, Integer.toString(getGrs()));
        result.put(JsonFields.FIELD_PARKING_BRAKE_POS, Integer.toString(getBrk()));
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

    public static ControlsInfo fromJsonObject(JsonObject obj) {
        ControlsInfo result = null;

        if (obj != null) {
            result = new ControlsInfo();

            result.setRdr(obj.getInt(JsonFields.FIELD_RUDDER_POS));
            result.setRdrtr(obj.getJsonNumber(JsonFields.FIELD_RUDDER_TRIM_POS).doubleValue());
            result.setEle(obj.getInt(JsonFields.FIELD_ELEVATOR_POS));
            result.setEletr(obj.getJsonNumber(JsonFields.FIELD_ELEVATOR_TRIM_POS).doubleValue());
            result.setAil(obj.getInt(JsonFields.FIELD_AILERON_POS));
            result.setAiltr(obj.getJsonNumber(JsonFields.FIELD_AILERON_TRIM_POS).doubleValue());
            result.setSpl(obj.getInt(JsonFields.FIELD_SPOILERS_POS));
            result.setFlp(obj.getJsonNumber(JsonFields.FIELD_FLAPS_POS).doubleValue());
            result.setGrs(obj.getInt(JsonFields.FIELD_GEARS_DOWN));
            result.setBrk(obj.getInt(JsonFields.FIELD_PARKING_BRAKE_POS));
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

    public void parse(String json) {
        if (json != null) {
            try (StringReader sr = new StringReader(json);
                 JsonReader jr = Json.createReader(sr)) {
                final JsonObject obj = jr.readObject();

                setRdr(obj.getInt(JsonFields.FIELD_RUDDER_POS));
                setRdrtr(obj.getJsonNumber(JsonFields.FIELD_RUDDER_TRIM_POS).doubleValue());
                setEle(obj.getInt(JsonFields.FIELD_ELEVATOR_POS));
                setEletr(obj.getJsonNumber(JsonFields.FIELD_ELEVATOR_TRIM_POS).doubleValue());
                setAil(obj.getInt(JsonFields.FIELD_AILERON_POS));
                setAiltr(obj.getJsonNumber(JsonFields.FIELD_AILERON_TRIM_POS).doubleValue());
                setSpl(obj.getInt(JsonFields.FIELD_SPOILERS_POS));
                setFlp(obj.getJsonNumber(JsonFields.FIELD_FLAPS_POS).doubleValue());
                setGrs(obj.getInt(JsonFields.FIELD_GEARS_DOWN));
                setBrk(obj.getInt(JsonFields.FIELD_PARKING_BRAKE_POS));
                setDr1(obj.getJsonNumber(JsonFields.FIELD_DOOR_POS).doubleValue());
            }
        }
    }

    public int getRdr() {
        return rdr;
    }

    public void setRdr(int rdr) {
        this.rdr = rdr;
    }

    public int getEle() {
        return ele;
    }

    public void setEle(int ele) {
        this.ele = ele;
    }

    public int getAil() {
        return ail;
    }

    public void setAil(int ail) {
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

    public int getSpl() {
        return spl;
    }

    public void setSpl(int spl) {
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

    public int getBrk() {
        return brk;
    }

    public void setBrk(int brk) {
        this.brk = brk;
    }

    public double getDr1() {
        return dr1;
    }

    public void setDr1(double dr1) {
        this.dr1 = dr1;
    }
}
