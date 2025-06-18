package cidade;

import java.util.Map;

public class TrafficLight {
    public String id;
    private double latitude;
    private double longitude;
    public Map<String, String> attributes;

    //construtor da class TrafficLight
    public TrafficLight(String id, double latitude, double longitude, Map<String, String> attributes) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.attributes = attributes;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }

    public String getDirection() {
        return attributes.getOrDefault("traffic_signals:direction", "forward");
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }
}
