// TravelRoutePlanner.java (Main class)
import java.util.*;
public class TravelRoutePlanner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Graph graph = new Graph();
        
        // Input: number of edges and each edge
        System.out.println("Enter number of edges:");
        int edges = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter edges in format \"CityA CityB Distance\":");
        for(int i = 0; i < edges; i++) {
            String[] parts = scanner.nextLine().split("\\s+");
            if(parts.length != 3) {
                System.out.println("Invalid input. Enter: City1 City2 Distance");
                i--;
                continue;
            }
            String c1 = parts[0], c2 = parts[1];
            int dist;
            try {
                dist = Integer.parseInt(parts[2]);
            } catch(NumberFormatException e) {
                System.out.println("Invalid distance (not a number).");
                i--;
                continue;
            }
            graph.addEdge(c1, c2, dist);
        }
        
        // Input: source and destination
        System.out.println("Enter source city:");
        String source = scanner.nextLine();
        System.out.println("Enter destination city:");
        String dest = scanner.nextLine();
        
        // 1) All paths via DFS
        System.out.println("\nAll paths from " + source + " to " + dest + ":");
        List<List<String>> allPaths = graph.findAllPaths(source, dest);
        for(List<String> path : allPaths) {
            System.out.println(String.join(" -> ", path));
        }
        
        // 2) All shortest-hop paths via BFS
        System.out.println("\nAll shortest (fewest-hop) paths from " + source + " to " + dest + ":");
        List<List<String>> shortestHopPaths = graph.findAllShortestHopPaths(source, dest);
        for(List<String> path : shortestHopPaths) {
            System.out.println(String.join(" -> ", path));
        }
        
        // 3) Shortest path by distance via Dijkstra
        System.out.println("\nShortest path by distance from " + source + " to " + dest + ":");
        List<String> shortestDistPath = graph.findShortestDistancePath(source, dest);
        if(shortestDistPath.isEmpty()) {
            System.out.println("No path found.");
        } else {
            // Calculate total distance of this path
            int totalDist = 0;
            for(int i = 0; i < shortestDistPath.size() - 1; i++) {
                String city1 = shortestDistPath.get(i);
                String city2 = shortestDistPath.get(i+1);
                CityNode node = graph.addCity(city1); // existing node
                for(Edge e : node.neighbors) {
                    if(e.target.name.equals(city2)) {
                        totalDist += e.weight;
                        break;
                    }
                }
            }
            System.out.println(String.join(" -> ", shortestDistPath) + "  (Distance: " + totalDist + ")");
        }
        scanner.close();
    }
}