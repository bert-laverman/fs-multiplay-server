package nl.rakis.fs;

public class UserInfo {
    private String id;
    private AircraftInfo aircraftInfo;
    private Location location;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AircraftInfo getAircraftInfo() {
        return aircraftInfo;
    }

    public void setAircraftInfo(AircraftInfo aircraftInfo) {
        this.aircraftInfo = aircraftInfo;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
