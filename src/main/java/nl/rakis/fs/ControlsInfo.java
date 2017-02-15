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

    private int    rudderPos;
    private int    elevatorPos;
    private int    aileronPos;
    private double rudderTrimPos;
    private double elevatorTrimPos;
    private double aileronTrimPos;
    private int    spoilersPos;
    private double flapsPos;
    private int    gearsDown;
    private int    parkingBrakePos;
    private double doorPos;

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
        result.put(JsonFields.FIELD_RUDDER_POS, Integer.toString(getRudderPos()));
        result.put(JsonFields.FIELD_RUDDER_TRIM_POS, Double.toString(getRudderTrimPos()));
        result.put(JsonFields.FIELD_ELEVATOR_POS, Integer.toString(getElevatorPos()));
        result.put(JsonFields.FIELD_ELEVATOR_TRIM_POS, Double.toString(getElevatorTrimPos()));
        result.put(JsonFields.FIELD_AILERON_POS, Integer.toString(getAileronPos()));
        result.put(JsonFields.FIELD_AILERON_TRIM_POS, Double.toString(getAileronTrimPos()));
        result.put(JsonFields.FIELD_SPOILERS_POS, Integer.toString(getSpoilersPos()));
        result.put(JsonFields.FIELD_FLAPS_POS, Double.toString(getFlapsPos()));
        result.put(JsonFields.FIELD_GEARS_DOWN, Integer.toString(getGearsDown()));
        result.put(JsonFields.FIELD_PARKING_BRAKE_POS, Integer.toString(getParkingBrakePos()));
        result.put(JsonFields.FIELD_DOOR_POS, Double.toString(getDoorPos()));

        return result;
    }

    @Override
    public JsonObject toJsonObject() {
        return Json.createObjectBuilder()
                .add(JsonFields.FIELD_TYPE, getType())
                .add(JsonFields.FIELD_RUDDER_POS, getRudderPos())
                .add(JsonFields.FIELD_RUDDER_TRIM_POS, getRudderTrimPos())
                .add(JsonFields.FIELD_ELEVATOR_POS, getElevatorPos())
                .add(JsonFields.FIELD_ELEVATOR_TRIM_POS, getElevatorTrimPos())
                .add(JsonFields.FIELD_AILERON_POS, getAileronPos())
                .add(JsonFields.FIELD_AILERON_TRIM_POS, getAileronTrimPos())
                .add(JsonFields.FIELD_SPOILERS_POS, getSpoilersPos())
                .add(JsonFields.FIELD_FLAPS_POS, getFlapsPos())
                .add(JsonFields.FIELD_GEARS_DOWN, getGearsDown())
                .add(JsonFields.FIELD_PARKING_BRAKE_POS, getParkingBrakePos())
                .add(JsonFields.FIELD_DOOR_POS, getDoorPos())
                .build();
    }

    public static ControlsInfo fromJsonObject(JsonObject obj) {
        ControlsInfo result = null;

        if (obj != null) {
            result = new ControlsInfo();

            result.setRudderPos(obj.getInt(JsonFields.FIELD_RUDDER_POS));
            result.setRudderTrimPos(obj.getJsonNumber(JsonFields.FIELD_RUDDER_TRIM_POS).doubleValue());
            result.setElevatorPos(obj.getInt(JsonFields.FIELD_ELEVATOR_POS));
            result.setElevatorTrimPos(obj.getJsonNumber(JsonFields.FIELD_ELEVATOR_TRIM_POS).doubleValue());
            result.setAileronPos(obj.getInt(JsonFields.FIELD_AILERON_POS));
            result.setAileronTrimPos(obj.getJsonNumber(JsonFields.FIELD_AILERON_TRIM_POS).doubleValue());
            result.setSpoilersPos(obj.getInt(JsonFields.FIELD_SPOILERS_POS));
            result.setFlapsPos(obj.getJsonNumber(JsonFields.FIELD_FLAPS_POS).doubleValue());
            result.setGearsDown(obj.getInt(JsonFields.FIELD_GEARS_DOWN));
            result.setParkingBrakePos(obj.getInt(JsonFields.FIELD_PARKING_BRAKE_POS));
            result.setDoorPos(obj.getJsonNumber(JsonFields.FIELD_DOOR_POS).doubleValue());
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

                setRudderPos(obj.getInt(JsonFields.FIELD_RUDDER_POS));
                setRudderTrimPos(obj.getJsonNumber(JsonFields.FIELD_RUDDER_TRIM_POS).doubleValue());
                setElevatorPos(obj.getInt(JsonFields.FIELD_ELEVATOR_POS));
                setElevatorTrimPos(obj.getJsonNumber(JsonFields.FIELD_ELEVATOR_TRIM_POS).doubleValue());
                setAileronPos(obj.getInt(JsonFields.FIELD_AILERON_POS));
                setAileronTrimPos(obj.getJsonNumber(JsonFields.FIELD_AILERON_TRIM_POS).doubleValue());
                setSpoilersPos(obj.getInt(JsonFields.FIELD_SPOILERS_POS));
                setFlapsPos(obj.getJsonNumber(JsonFields.FIELD_FLAPS_POS).doubleValue());
                setGearsDown(obj.getInt(JsonFields.FIELD_GEARS_DOWN));
                setParkingBrakePos(obj.getInt(JsonFields.FIELD_PARKING_BRAKE_POS));
                setDoorPos(obj.getJsonNumber(JsonFields.FIELD_DOOR_POS).doubleValue());
            }
        }
    }

    public int getRudderPos() {
        return rudderPos;
    }

    public void setRudderPos(int rudderPos) {
        this.rudderPos = rudderPos;
    }

    public int getElevatorPos() {
        return elevatorPos;
    }

    public void setElevatorPos(int elevatorPos) {
        this.elevatorPos = elevatorPos;
    }

    public int getAileronPos() {
        return aileronPos;
    }

    public void setAileronPos(int aileronPos) {
        this.aileronPos = aileronPos;
    }

    public double getRudderTrimPos() {
        return rudderTrimPos;
    }

    public void setRudderTrimPos(double rudderTrimPos) {
        this.rudderTrimPos = rudderTrimPos;
    }

    public double getElevatorTrimPos() {
        return elevatorTrimPos;
    }

    public void setElevatorTrimPos(double elevatorTrimPos) {
        this.elevatorTrimPos = elevatorTrimPos;
    }

    public double getAileronTrimPos() {
        return aileronTrimPos;
    }

    public void setAileronTrimPos(double aileronTrimPos) {
        this.aileronTrimPos = aileronTrimPos;
    }

    public int getSpoilersPos() {
        return spoilersPos;
    }

    public void setSpoilersPos(int spoilersPos) {
        this.spoilersPos = spoilersPos;
    }

    public double getFlapsPos() {
        return flapsPos;
    }

    public void setFlapsPos(double flapsPos) {
        this.flapsPos = flapsPos;
    }

    public int getGearsDown() {
        return gearsDown;
    }

    public void setGearsDown(int gearsDown) {
        this.gearsDown = gearsDown;
    }

    public int getParkingBrakePos() {
        return parkingBrakePos;
    }

    public void setParkingBrakePos(int parkingBrakePos) {
        this.parkingBrakePos = parkingBrakePos;
    }

    public double getDoorPos() {
        return doorPos;
    }

    public void setDoorPos(double doorPos) {
        this.doorPos = doorPos;
    }
}
