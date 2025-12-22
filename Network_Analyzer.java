/*
Author Name: Cooper Huntington-Bugg
Email: cohunti@okstate.edu
Date: 11/23/2025
Program Description:
This program builds an undirected unweighted network graph for infrastructure nodes
from an input file. The graph is stored as an adjacency list. The user can remove
connections, delete nodes, count connections, list connection groups for a unit,
compute closeness centrality for a node and find connector nodes in the
network. All interaction is done through a menu on the console.
*/

import java.io.*;
import java.util.*;

// Main driver class
public class Network_Analyzer {

    // Entry point of the program
    // args - command line arguments (not used)
    public static void main(String[] args) {
        try (Scanner console = new Scanner(System.in)) {
            System.out.print("Enter input filename: ");
            String filename = console.nextLine().trim();

            Graph graph;
            try {
                graph = Graph.fromFile(filename);
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
                return;
            }

            System.out.println("Input file is read successfully..");
            System.out.println("Total number of vertices in the graph: " + graph.getVertexCount());
            System.out.println("Total number of edges in the graph: " + graph.getEdgeCount());

            boolean exit = false;

            while (!exit) {
                printMenu();
                System.out.print("Enter your choice: ");

                int choice;
                if (!console.hasNextInt()) {
                    // Clear incorrect input
                    console.nextLine();
                    System.out.println("Invalid option. Please try again.");
                    continue;
                }
                choice = console.nextInt();
                console.nextLine(); // consume end of line

                switch (choice) {
                    case 1:
                        handleRemoveConnection(console, graph);
                        break;
                    case 2:
                        handleDeleteNode(console, graph);
                        break;
                    case 3:
                        handleCountConnections(console, graph);
                        break;
                    case 4:
                        handleConnectionGroups(console, graph);
                        break;
                    case 5:
                        handleClosenessCentrality(console, graph);
                        break;
                    case 6:
                        handleFindConnectors(graph);
                        break;
                    case 7:
                        exit = true;
                        System.out.println("Exiting from the program.");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                        break;
                }
            }
        }
    }

    // Prints the main menu for the user
    private static void printMenu() {
        System.out.println();
        System.out.println("1. Remove connection");
        System.out.println("2. Delete Node");
        System.out.println("3. Count connections");
        System.out.println("4. Connection Groups");
        System.out.println("5. Closeness centrality");
        System.out.println("6. Find Connectors");
        System.out.println("7. Exit");
    }

    // Handles option 1: removing a connection between two nodes
    private static void handleRemoveConnection(Scanner in, Graph graph) {
        System.out.print("Enter first node's name: ");
        String name1 = in.nextLine().trim();
        System.out.print("Enter second node's name: ");
        String name2 = in.nextLine().trim();

        Vertex v1 = graph.findByNodeName(name1);
        Vertex v2 = graph.findByNodeName(name2);

        boolean missing = false;

        if (v1 == null) {
            System.out.println("Sorry.. ");
            System.out.println(name1 + " not found!");
            missing = true;
        }

        if (v2 == null) {
            System.out.println("Sorry.. ");
            System.out.println(name2 + " not found!");
            missing = true;
        }

        if (missing) {
            return;
        }

        assert v1 != null && v2 != null; // Already checked above

        boolean removed = graph.removeConnection(v1, v2);

        if (removed) {
            System.out.println("The edge between the nodes " 
                + v1.node.nodeName + " and " + v2.node.nodeName
                + " has been successfully removed..");
            System.out.println("Total number of nodes in the graph: " + graph.getVertexCount());
            System.out.println("Total number of edges in the graph: " + graph.getEdgeCount());
        } else {
            System.out.println("Sorry.. There is no edge between the vertices "
                + v1.node.nodeName + " and " + v2.node.nodeName + ".");
        }
    }

    // Handles option 2: deleting a node from the graph
    private static void handleDeleteNode(Scanner in, Graph graph) {
        System.out.print("Enter node's name to delete: ");
        String name = in.nextLine().trim();

        Vertex v = graph.findByNodeName(name);

        if (v == null) {
            System.out.println("Sorry.. ");
            System.out.println(name + " not found!");
            return;
        }

        graph.deleteVertex(v);

        System.out.println("The node " + name + " has been successfully removed..");
        System.out.println("Total number of vertices in the graph: " + graph.getVertexCount());
        System.out.println("Total number of edges in the graph: " + graph.getEdgeCount());
    }

    // Handles option 3: counting connections for a node and listing them
    private static void handleCountConnections(Scanner in, Graph graph) {
        System.out.print("Enter node's name: ");
        String name = in.nextLine().trim();

        Vertex v = graph.findByNodeName(name);

        if (v == null) {
            System.out.println("Sorry.. ");
            System.out.println(name + " not found!");
            return;
        }

        int count = v.neighbors.size();

        System.out.println("Connection count for " + v.node.nodeName + ": " + count);
        System.out.println("Connections of " + v.node.nodeName + " are:");

        for (Vertex conn : v.neighbors) {
            System.out.println(conn.node.nodeName);
        }
    }

    // Handles option 4: listing connection groups for a given unit
    private static void handleConnectionGroups(Scanner in, Graph graph) {
        System.out.print("Enter unit name: ");
        String unit = in.nextLine().trim();

        List<List<Vertex>> connectionGroups = graph.getConnectionGroupsForUnit(unit);

        System.out.println("Following are the connection groups in the unit " + unit + ":");

        if (connectionGroups.isEmpty()) {
            // It is possible that there are no nodes from that unit.
            return;
        }

        for (List<Vertex> group : connectionGroups) {
            if (group.size() == 1) {
                System.out.println(group.get(0).node.nodeName);
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < group.size(); i++) {
                    sb.append(group.get(i).node.nodeName);
                    if (i != group.size() - 1) {
                        sb.append(" - ");
                    }
                }
                System.out.println(sb.toString());
            }
        }
    }

    // Handles option 5: computing closeness centrality for a student
    // in - scanner used for user input
    // graph - current social graph
    private static void handleClosenessCentrality(Scanner in, Graph graph) {
        System.out.print("Enter node's name: ");
        String name = in.nextLine().trim();

        Vertex v = graph.findByNodeName(name);

        if (v == null) {
            System.out.println("Sorry.. ");
            System.out.println(name + " not found!");
            return;
        }

        double closeness = graph.computeClosenessCentrality(v);
        double normalized = 0.0;
        if (graph.getVertexCount() > 1) {
            normalized = closeness / (graph.getVertexCount() - 1);
        }

        System.out.printf("The Closeness Centrality for %s: %.2f%n",
            v.node.nodeName, closeness);
        System.out.printf("The Normalized Closeness Centrality for %s: %.2f%n",
            v.node.nodeName, normalized);
    }

    // Handles option 6 (bonus): finding connector vertices in the graph
    // graph - current social graph
    private static void handleFindConnectors(Graph graph) {
        List<Vertex> connectors = graph.findConnectors();

        if (connectors.isEmpty()) {
            System.out.println("There are no connectors in the graph.");
            return;
        }

        System.out.println("The connectors in the graph are as follows:");
        for (Vertex v : connectors) {
            System.out.println(v.node.nodeName + " from " + v.node.unit);
        }
    }
}

// Simple data class that stores node information
class Node {
    long id;
    String nodeName;
    String group;
    String unit;
    String contact;

    // Builds a node object with all fields
    public Node(long id, String nodeName, String group, String unit, String contact) {
        this.id = id;
        this.nodeName = nodeName;
        this.group = group;
        this.unit = unit;
        this.contact = contact;
    }
}

// Vertex class for the graph. Wraps a node and its adjacency list
class Vertex {
    Node node;
    List<Vertex> neighbors;

    // Fields used for connector computation
    int dfsNum;
    int low;

    // Builds a vertex for a given node
    public Vertex(Node node) {
        this.node = node;
        this.neighbors = new ArrayList<>();
        this.dfsNum = 0;
        this.low = 0;
    }
}

// Helper class that stores pending connection ids while building the graph
class PendingConnections {
    long ownerId;
    List<Long> connectionIds;

    public PendingConnections(long ownerId, List<Long> connectionIds) {
        this.ownerId = ownerId;
        this.connectionIds = connectionIds;
    }
}

// Graph class that stores the social network as an adjacency list
class Graph {

    private final Map<Long, Vertex> vertices;
    private int edgeCount;

    // Constructs an empty graph
    public Graph() {
        this.vertices = new LinkedHashMap<>();
        this.edgeCount = 0;
    }

    // Reads the graph from the given input file and returns a new Graph
    public static Graph fromFile(String filename) throws IOException {
        Graph graph = new Graph();
        List<PendingConnections> pending = new ArrayList<>();
        File file = new File(filename);
        try (Scanner fileScanner = new Scanner(file)) {
            if (!fileScanner.hasNextLine()) {
                return graph;
            }
            // Skip header line
            fileScanner.nextLine();
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] tokens = line.split("\t");
                if (tokens.length < 6) {
                    continue;
                }
                long id = Long.parseLong(tokens[0].trim());
                String nodeName = tokens[1].trim();
                String group = tokens[2].trim();
                String unit = tokens[3].trim();
                String contact = tokens[4].trim();
                int connectionCount = Integer.parseInt(tokens[5].trim());
                Node n = new Node(id, nodeName, group, unit, contact);
                graph.addVertex(n);
                List<Long> connections = new ArrayList<>();
                for (int i = 0; i < connectionCount; i++) {
                    int idx = 6 + i;
                    if (idx < tokens.length) {
                        String c = tokens[idx].trim();
                        if (!c.isEmpty()) {
                            long connId = Long.parseLong(c);
                            connections.add(connId);
                        }
                    }
                }
                pending.add(new PendingConnections(id, connections));
            }
        }
        // Second pass: add undirected edges
        for (PendingConnections pc : pending) {
            for (Long connId : pc.connectionIds) {
                long a = pc.ownerId;
                long b = connId;
                if (a == b) {
                    continue;
                }
                if (a < b) {
                    graph.addConnectionById(a, b);
                }
            }
        }
        return graph;
    }

    // Adds a vertex for the given node if it does not already exist
    public void addVertex(Node node) {
        if (!vertices.containsKey(node.id)) {
            vertices.put(node.id, new Vertex(node));
        }
    }

    // Adds an undirected connection between two nodes given their ids
    public void addConnectionById(long id1, long id2) {
        Vertex v1 = vertices.get(id1);
        Vertex v2 = vertices.get(id2);
        if (v1 == null || v2 == null) {
            return;
        }
        addConnection(v1, v2);
    }

    // Adds an undirected edge between two vertices if it does not already exist
    private void addConnection(Vertex v1, Vertex v2) {
        if (v1 == v2) {
            return;
        }
        if (!v1.neighbors.contains(v2)) {
            v1.neighbors.add(v2);
            v2.neighbors.add(v1);
            edgeCount++;
        }
    }

    // Removes an undirected edge between two vertices when it exists
    public boolean removeConnection(Vertex v1, Vertex v2) {
        boolean removed1 = v1.neighbors.remove(v2);
        boolean removed2 = v2.neighbors.remove(v1);
        if (removed1 && removed2) {
            edgeCount--;
            return true;
        }
        if (removed1 ^ removed2) {
            if (removed1) {
                v2.neighbors.remove(v1);
            } else {
                v1.neighbors.remove(v2);
            }
        }
        return false;
    }

    // Deletes a vertex from the graph and removes all of its incident edges
    // v - vertex to delete
    public void deleteVertex(Vertex v) {
        // For each neighbor, remove back reference and update edge count
        for (Vertex neighbor : new ArrayList<>(v.neighbors)) {
            neighbor.neighbors.remove(v);
            edgeCount--;
        }
        v.neighbors.clear();

        vertices.remove(v.node.id);
    }

    // Returns the number of vertices in the graph
    // returns vertex count
    public int getVertexCount() {
        return vertices.size();
    }

    // Returns the number of edges in the graph
    // returns edge count
    public int getEdgeCount() {
        return edgeCount;
    }

    // Finds the vertex by node name, ignoring case
    public Vertex findByNodeName(String nodeName) {
        for (Vertex v : vertices.values()) {
            if (v.node.nodeName.equalsIgnoreCase(nodeName)) {
                return v;
            }
        }
        return null;
    }

    // Computes connection groups for a given unit using connected components
    public List<List<Vertex>> getConnectionGroupsForUnit(String unitName) {
        List<List<Vertex>> groups = new ArrayList<>();
        Set<Vertex> visited = new HashSet<>();
        for (Vertex start : vertices.values()) {
            if (visited.contains(start)) {
                continue;
            }
            List<Vertex> component = new ArrayList<>();
            Queue<Vertex> queue = new LinkedList<>();
            visited.add(start);
            queue.add(start);
            while (!queue.isEmpty()) {
                Vertex current = queue.poll();
                component.add(current);
                for (Vertex neighbor : current.neighbors) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
            // Filter this component to only nodes from the given unit
            List<Vertex> inUnit = new ArrayList<>();
            for (Vertex v : component) {
                if (v.node.unit.equalsIgnoreCase(unitName)) {
                    inUnit.add(v);
                }
            }
            if (!inUnit.isEmpty()) {
                groups.add(inUnit);
            }
        }
        return groups;
    }

    // Computes the closeness centrality for a given vertex using Dijkstra's algorithm
    // The graph is unweighted, so all edges have weight 1
    // start - starting vertex
    // returns closeness centrality value
    public double computeClosenessCentrality(Vertex start) {
        Map<Vertex, Double> dist = new HashMap<>();
        for (Vertex v : vertices.values()) {
            dist.put(v, Double.POSITIVE_INFINITY);
        }

        // Inner class for priority queue entries
        class PQNode {
            Vertex v;
            double d;
            PQNode(Vertex v, double d) {
                this.v = v;
                this.d = d;
            }
        }

        PriorityQueue<PQNode> pq = new PriorityQueue<>((a, b) -> Double.compare(a.d, b.d));

        dist.put(start, 0.0);
        pq.add(new PQNode(start, 0.0));

        while (!pq.isEmpty()) {
            PQNode current = pq.poll();
            if (current.d > dist.get(current.v)) {
                continue;
            }
            for (Vertex neighbor : current.v.neighbors) {
                double newDist = current.d + 1.0;
                if (newDist < dist.get(neighbor)) {
                    dist.put(neighbor, newDist);
                    pq.add(new PQNode(neighbor, newDist));
                }
            }
        }

        double sum = 0.0;
        for (Map.Entry<Vertex, Double> entry : dist.entrySet()) {
            Vertex v = entry.getKey();
            if (v == start) {
                continue;
            }
            double d = entry.getValue();
            if (d != Double.POSITIVE_INFINITY && d > 0.0) {
                sum += 1.0 / d;
            }
        }
        return sum;
    }

    // Finds connector vertices in the graph using DFS and low values
    // A connector is a vertex whose removal increases the number of
    // connected components in its connected component
    // returns list of connector vertices
    public List<Vertex> findConnectors() {
        // Reset dfs numbers and low values
        for (Vertex v : vertices.values()) {
            v.dfsNum = 0;
            v.low = 0;
        }

        List<Vertex> connectors = new ArrayList<>();
        Set<Vertex> connectorSet = new LinkedHashSet<>();

        int[] time = new int[1]; // time[0] will act as shared counter
        time[0] = 1;

        for (Vertex v : vertices.values()) {
            if (v.dfsNum == 0) {
                dfsConnector(v, null, time, connectorSet);
            }
        }

        connectors.addAll(connectorSet);
        return connectors;
    }

    // Recursive helper method that performs DFS and computes dfsNum and low
    // Articulation point conditions are checked during traversal
    // v - current vertex
    // parent - parent vertex in the DFS tree (null for root)
    // time - shared time counter
    // connectorSet - set that collects connectors
    private void dfsConnector(Vertex v, Vertex parent, int[] time, Set<Vertex> connectorSet) {
        v.dfsNum = time[0];
        v.low = time[0];
        time[0]++;

        int childCount = 0;

        for (Vertex neighbor : v.neighbors) {
            if (neighbor == parent) {
                continue;
            }

            if (neighbor.dfsNum == 0) {
                childCount++;
                dfsConnector(neighbor, v, time, connectorSet);

                // Update low value for v from child
                v.low = Math.min(v.low, neighbor.low);

                // Articulation point check for non root vertices
                if (parent != null && neighbor.low >= v.dfsNum) {
                    connectorSet.add(v);
                }
            } else {
                // Back edge case
                v.low = Math.min(v.low, neighbor.dfsNum);
            }
        }

        // Root articulation check
        if (parent == null && childCount > 1) {
            connectorSet.add(v);
        }
    }
}