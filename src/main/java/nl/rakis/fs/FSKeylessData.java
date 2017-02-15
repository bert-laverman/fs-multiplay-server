package nl.rakis.fs;

/**
 * Data which is unique per session+callsign, but has no key of it self.
 */
public abstract class FSKeylessData
    extends FSData
{
    private String type;

    public FSKeylessData(String type) {
        this.type = type;
    }

    public String getKey(String session, String callsign) {
        return type + ":" + session + ":" + callsign;
    }
}
