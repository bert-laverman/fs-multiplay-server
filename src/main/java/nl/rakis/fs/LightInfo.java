package nl.rakis.fs;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The state of the lights.
 */
public class LightInfo
    extends FSKeylessData
{

    private static final Logger log = Logger.getLogger(LightInfo.class.getName());

    public static final String TYPE_LIGHTS = "Lights";

    boolean strb;
    boolean land;
    boolean taxi;
    boolean bcn;
    boolean nav;
    boolean logo;
    boolean wing;
    boolean recg;
    boolean cabn;

    public LightInfo() {
        super(getType());

        strb = false;
        land = false;
        taxi = false;
        bcn = false;
        nav = false;
        logo = false;
        wing = false;
        recg = false;
        cabn = false;
    }

    @Override
    public String getKey() {
        return "";
    }

    public static String getType() {
        return TYPE_LIGHTS;
    }

    @Override
    public Map<String, String> toMap() {
        Map<String,String> result = new HashMap<>();

        result.put(JsonFields.FIELD_TYPE, getType());
        result.put(JsonFields.FIELD_STRB, Boolean.toString(isStrb()));
        result.put(JsonFields.FIELD_LAND, Boolean.toString(isLand()));
        result.put(JsonFields.FIELD_TAXI, Boolean.toString(isTaxi()));
        result.put(JsonFields.FIELD_BCN, Boolean.toString(isBcn()));
        result.put(JsonFields.FIELD_NAV, Boolean.toString(isNav()));
        result.put(JsonFields.FIELD_LOGO, Boolean.toString(isLogo()));
        result.put(JsonFields.FIELD_WING, Boolean.toString(isWing()));
        result.put(JsonFields.FIELD_RECG, Boolean.toString(isRecg()));
        result.put(JsonFields.FIELD_CABN, Boolean.toString(isCabn()));

        return result;
    }

    @Override
    public JsonObject toJsonObject() {
        return Json.createObjectBuilder()
                .add(JsonFields.FIELD_TYPE, getType())
                .add(JsonFields.FIELD_STRB, isStrb())
                .add(JsonFields.FIELD_LAND, isLand())
                .add(JsonFields.FIELD_TAXI, isTaxi())
                .add(JsonFields.FIELD_BCN, isBcn())
                .add(JsonFields.FIELD_NAV, isNav())
                .add(JsonFields.FIELD_LOGO, isLogo())
                .add(JsonFields.FIELD_WING, isWing())
                .add(JsonFields.FIELD_RECG, isRecg())
                .add(JsonFields.FIELD_CABN, isCabn())
                .build();
    }

    public static LightInfo fromJsonObject(JsonObject obj) {
        LightInfo result = null;

        if (obj != null) {
            result = new LightInfo();

            result.setStrb(getBoolIf(obj, JsonFields.FIELD_STRB, false));
            result.setLand(getBoolIf(obj, JsonFields.FIELD_LAND, false));
            result.setTaxi(getBoolIf(obj, JsonFields.FIELD_TAXI, false));
            result.setBcn(getBoolIf(obj, JsonFields.FIELD_BCN, false));
            result.setNav(getBoolIf(obj, JsonFields.FIELD_NAV, false));
            result.setLogo(getBoolIf(obj, JsonFields.FIELD_LOGO, false));
            result.setWing(getBoolIf(obj, JsonFields.FIELD_WING, false));
            result.setRecg(getBoolIf(obj, JsonFields.FIELD_RECG, false));
            result.setCabn(getBoolIf(obj, JsonFields.FIELD_CABN, false));
        }

        return result;
    }

    public static LightInfo fromString(String json) {
        LightInfo result = null;

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

                setStrb(getBoolIf(obj, JsonFields.FIELD_STRB, false));
                setLand(getBoolIf(obj, JsonFields.FIELD_LAND, false));
                setTaxi(getBoolIf(obj, JsonFields.FIELD_TAXI, false));
                setBcn(getBoolIf(obj, JsonFields.FIELD_BCN, false));
                setNav(getBoolIf(obj, JsonFields.FIELD_NAV, false));
                setLogo(getBoolIf(obj, JsonFields.FIELD_LOGO, false));
                setWing(getBoolIf(obj, JsonFields.FIELD_WING, false));
                setRecg(getBoolIf(obj, JsonFields.FIELD_RECG, false));
                setCabn(getBoolIf(obj, JsonFields.FIELD_CABN, false));
            }
        }
    }

    public boolean isStrb() {
        return strb;
    }

    public void setStrb(boolean strb) {
        this.strb = strb;
    }

    public boolean isLand() {
        return land;
    }

    public void setLand(boolean land) {
        this.land = land;
    }

    public boolean isTaxi() {
        return taxi;
    }

    public void setTaxi(boolean taxi) {
        this.taxi = taxi;
    }

    public boolean isBcn() {
        return bcn;
    }

    public void setBcn(boolean bcn) {
        this.bcn = bcn;
    }

    public boolean isNav() {
        return nav;
    }

    public void setNav(boolean nav) {
        this.nav = nav;
    }

    public boolean isLogo() {
        return logo;
    }

    public void setLogo(boolean logo) {
        this.logo = logo;
    }

    public boolean isWing() {
        return wing;
    }

    public void setWing(boolean wing) {
        this.wing = wing;
    }

    public boolean isRecg() {
        return recg;
    }

    public void setRecg(boolean recg) {
        this.recg = recg;
    }

    public boolean isCabn() {
        return cabn;
    }

    public void setCabn(boolean cabn) {
        this.cabn = cabn;
    }
}
