package cidade;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GraphData {
    public List<Node> nodes;
    public List<Edge> edges;
    @SerializedName("traffic_lights")
    public List<TrafficLight> trafficLights; // campo para os semáforos
}
