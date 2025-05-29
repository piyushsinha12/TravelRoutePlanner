// Graph.java
import java.util.*;
public class Graph {
    private Map<String, CityNode> cityMap = new HashMap<>();
    
    // Add a city to the graph (if not exists) and return it
    public CityNode addCity(String name) {
        return cityMap.computeIfAbsent(name, CityNode::new);
    }
    // Add an undirected edge between city1 and city2 with the given distance
    public void addEdge(String city1, String city2, int distance) {
        CityNode c1 = addCity(city1);
        CityNode c2 = addCity(city2);
        c1.neighbors.add(new Edge(c2, distance));
        c2.neighbors.add(new Edge(c1, distance));
    }
    
    // 1) DFS: Find **all paths** (no revisiting) from src to dest
    public List<List<String>> findAllPaths(String srcName, String destName) {
        List<List<String>> paths = new ArrayList<>();
        if(!cityMap.containsKey(srcName) || !cityMap.containsKey(destName))
            return paths;
        CityNode src = cityMap.get(srcName), dest = cityMap.get(destName);
        Set<CityNode> visited = new HashSet<>();
        List<String> currentPath = new ArrayList<>();
        currentPath.add(src.name);
        dfsHelper(src, dest, visited, currentPath, paths);
        return paths;
    }
    private void dfsHelper(CityNode current, CityNode dest, Set<CityNode> visited, 
                           List<String> path, List<List<String>> paths) {
        if(current.equals(dest)) {
            // Reached destination, record this path
            paths.add(new ArrayList<>(path));
            return;
        }
        visited.add(current);
        for(Edge edge : current.neighbors) {
            CityNode next = edge.target;
            if(!visited.contains(next)) {
                path.add(next.name);
                dfsHelper(next, dest, visited, path, paths);
                path.remove(path.size() - 1);
            }
        }
        visited.remove(current);
    }
    
    // 2) BFS: Find **all shortest (fewest-hop) paths** from src to dest
    public List<List<String>> findAllShortestHopPaths(String srcName, String destName) {
        List<List<String>> allPaths = new ArrayList<>();
        if(!cityMap.containsKey(srcName) || !cityMap.containsKey(destName))
            return allPaths;
        CityNode src = cityMap.get(srcName), dest = cityMap.get(destName);
        
        // Initialize distance and parent lists for BFS
        Map<CityNode,Integer> dist = new HashMap<>();
        Map<CityNode,List<CityNode>> parents = new HashMap<>();
        for(CityNode node : cityMap.values()) {
            dist.put(node, Integer.MAX_VALUE);
            parents.put(node, new ArrayList<>());
        }
        Queue<CityNode> queue = new LinkedList<>();
        dist.put(src, 0);
        parents.get(src).add(null); // mark source with null parent
        queue.add(src);
        
        // BFS traversal to compute shortest distances and parents
        while(!queue.isEmpty()) {
            CityNode u = queue.poll();
            for(Edge edge : u.neighbors) {
                CityNode v = edge.target;
                // Found shorter path to v?
                if(dist.get(v) > dist.get(u) + 1) {
                    dist.put(v, dist.get(u) + 1);
                    parents.get(v).clear();
                    parents.get(v).add(u);
                    queue.add(v);
                } else if(dist.get(v) == dist.get(u) + 1) {
                    // Found an alternative parent for same shortest distance
                    parents.get(v).add(u);
                }
            }
        }
        
        // Reconstruct all paths from dest back to src using parents
        List<CityNode> current = new ArrayList<>();
        findPathsFromParents(dest, parents, current, allPaths);
        // Reverse each path to be from src->dest
        List<List<String>> resultPaths = new ArrayList<>();
        for(List<String> p : allPaths) {
            Collections.reverse(p);
            resultPaths.add(p);
        }
        return resultPaths;
    }
    // Helper: recursively build paths using parent pointers (backwards)
    private void findPathsFromParents(CityNode node, Map<CityNode,List<CityNode>> parents,
                                      List<CityNode> path, List<List<String>> paths) {
        if(node == null) {
            // Reached the start (null parent): record this reversed path of names
            List<String> singlePath = new ArrayList<>();
            for(CityNode n : path) singlePath.add(n.name);
            paths.add(new ArrayList<>(singlePath));
            return;
        }
        path.add(node);
        for(CityNode par : parents.get(node)) {
            findPathsFromParents(par, parents, path, paths);
        }
        path.remove(path.size() - 1);
    }
    
    // 3) Dijkstra: Find **shortest path by distance** from src to dest
    public List<String> findShortestDistancePath(String srcName, String destName) {
        List<String> path = new ArrayList<>();
        if(!cityMap.containsKey(srcName) || !cityMap.containsKey(destName))
            return path;
        CityNode src = cityMap.get(srcName), dest = cityMap.get(destName);
        
        // Setup distances and predecessors
        Map<CityNode,Integer> dist = new HashMap<>();
        Map<CityNode,CityNode> prev = new HashMap<>();
        for(CityNode node : cityMap.values()) {
            dist.put(node, Integer.MAX_VALUE);
            prev.put(node, null);
        }
        dist.put(src, 0);
        
        // Min-heap priority queue based on current dist
        PriorityQueue<CityNode> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));
        pq.add(src);
        
        // Standard Dijkstra loop
        while(!pq.isEmpty()) {
            CityNode u = pq.poll();
            if(u.equals(dest)) break; // stop early if desired
            for(Edge edge : u.neighbors) {
                CityNode v = edge.target;
                int newDist = dist.get(u) + edge.weight;
                if(newDist < dist.get(v)) {
                    dist.put(v, newDist);
                    prev.put(v, u);
                    pq.add(v);
                }
            }
        }
        // Reconstruct shortest path from dest to src
        for(CityNode at = dest; at != null; at = prev.get(at)) {
            path.add(at.name);
        }
        Collections.reverse(path);
        return path;
    }
}