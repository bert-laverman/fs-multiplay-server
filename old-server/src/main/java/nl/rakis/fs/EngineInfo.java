package nl.rakis.fs;

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

    public void parse(String json) {
        if (json != null) {
            try (StringReader sr = new StringReader(json);
                 JsonReader jr = Json.createReader(sr)) {
                final JsonObject obj = jr.readObject();

                setEng(copyArray(obj.getJsonArray(JsonFields.FIELD_ENGINE_ON), 4));
                setThrt(copyArray(obj.getJsonArray(JsonFields.FIELD_THROTTLE), 4));
            }
        }
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
