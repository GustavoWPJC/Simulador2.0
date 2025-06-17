package cidade;

public class Edge {
    public String id;
    public String source;
    public String target;
    public double length;
    public double travel_time;
    public boolean oneway;
    public double maxspeed;

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }


    @Override
    public String toString() {
        return "Edge{" +
                "id='" + id + '\'' +
                ", source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", length=" + length +
                ", travel_time=" + travel_time +
                ", oneway=" + oneway +
                ", maxspeed=" + maxspeed +
                '}';
    }
}
