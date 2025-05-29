// CityNode.java
import java.util.*;
public class CityNode {
    String name;
    List<Edge> neighbors; // adjacent cities and their distances
    public CityNode(String name) {
        this.name = name;
        neighbors = new ArrayList<>();
    }
    @Override
    public String toString() {
        return name;
    }
}

// Edge.java
class Edge {
    CityNode target;
    int weight;
    public Edge(CityNode target, int weight) {
        this.target = target;
        this.weight = weight;
    }
}