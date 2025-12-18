/*
Author Name: Cooper Huntington-Bugg
Email: cohunti@okstate.edu
Date: 11/23/2025
Program Description:
This program builds an undirected unweighted social network graph for OSU students
from an input file. The graph is stored as an adjacency list. The user can remove
friendships, delete accounts, count friends, list friend circles for a college,
compute closeness centrality for a student and find connector students in the
network. All interaction is done through a menu on the console.
*/

import java.io.*;
import java.util.*;

// Main driver class for Assignment 05
public class StudentNetworkAnalyzer {

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
                        handleRemoveFriendship(console, graph);
                        break;
                    case 2:
                        handleDeleteAccount(console, graph);
                        break;
                    case 3:
                        handleCountFriends(console, graph);
                        break;
                    case 4:
                        handleFriendsCircle(console, graph);
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
        System.out.println("1. Remove friendship");
        System.out.println("2. Delete Account");
        System.out.println("3. Count friends");
        System.out.println("4. Friends Circle");
        System.out.println("5. Closeness centrality");
        System.out.println("6. Find Connectors");
        System.out.println("7. Exit");
    }

    // Handles option 1: removing a friendship between two students
    // in - scanner used for user input
    // graph - current social graph
    private static void handleRemoveFriendship(Scanner in, Graph graph) {
        System.out.print("Enter first student's first name: ");
        String name1 = in.nextLine().trim();
        System.out.print("Enter second student's first name: ");
        String name2 = in.nextLine().trim();

        Vertex v1 = graph.findByFirstName(name1);
        Vertex v2 = graph.findByFirstName(name2);

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

        boolean removed = graph.removeFriendship(v1, v2);

        if (removed) {
            System.out.println("The edge between the students " 
                    + v1.student.firstName + " and " + v2.student.firstName
                    + " has been successfully removed..");
            System.out.println("Total number of students in the graph: " + graph.getVertexCount());
            System.out.println("Total number of edges in the graph: " + graph.getEdgeCount());
        } else {
            System.out.println("Sorry.. There is no edge between the vertices "
                    + v1.student.firstName + " and " + v2.student.firstName + ".");
        }
    }

    // Handles option 2: deleting a student account from the graph
    // in - scanner used for user input
    // graph - current social graph
    private static void handleDeleteAccount(Scanner in, Graph graph) {
        System.out.print("Enter student's first name to delete: ");
        String name = in.nextLine().trim();

        Vertex v = graph.findByFirstName(name);

        if (v == null) {
            System.out.println("Sorry.. ");
            System.out.println(name + " not found!");
            return;
        }

        graph.deleteVertex(v);

        System.out.println("The student " + name + " has been successfully removed..");
        System.out.println("Total number of vertices in the graph: " + graph.getVertexCount());
        System.out.println("Total number of edges in the graph: " + graph.getEdgeCount());
    }

    // Handles option 3: counting friends for a student and listing them
    // in - scanner used for user input
    // graph - current social graph
    private static void handleCountFriends(Scanner in, Graph graph) {
        System.out.print("Enter student's first name: ");
        String name = in.nextLine().trim();

        Vertex v = graph.findByFirstName(name);

        if (v == null) {
            System.out.println("Sorry.. ");
            System.out.println(name + " not found!");
            return;
        }

        int count = v.neighbors.size();

        System.out.println("Friend count for " + v.student.firstName + ": " + count);
        System.out.println("Friends of " + v.student.firstName + " are:");

        for (Vertex friend : v.neighbors) {
            System.out.println(friend.student.firstName);
        }
    }

    // Handles option 4: listing friend circles for a given college
    // in - scanner used for user input
    // graph - current social graph
    private static void handleFriendsCircle(Scanner in, Graph graph) {
        System.out.print("Enter college name: ");
        String college = in.nextLine().trim();

        List<List<Vertex>> friendCircles = graph.getFriendCirclesForCollege(college);

        System.out.println("Following are the friend circles in the " + college + ":");

        if (friendCircles.isEmpty()) {
            // It is possible that there are no students from that college.
            return;
        }

        for (List<Vertex> circle : friendCircles) {
            if (circle.size() == 1) {
                System.out.println(circle.get(0).student.firstName);
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < circle.size(); i++) {
                    sb.append(circle.get(i).student.firstName);
                    if (i != circle.size() - 1) {
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
        System.out.print("Enter student's first name: ");
        String name = in.nextLine().trim();

        Vertex v = graph.findByFirstName(name);

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
                v.student.firstName, closeness);
        System.out.printf("The Normalized Closeness Centrality for %s: %.2f%n",
                v.student.firstName, normalized);
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
            System.out.println(v.student.firstName + " from " + v.student.college);
        }
    }
}

// Simple data class that stores student information
class Student {
    long id;
    String firstName;
    String lastName;      // Not directly used but part of complete student record
    String college;
    String department;    // Not directly used but part of complete student record
    String email;         // Not directly used but part of complete student record

    // Builds a student object with all fields
    // id - unique student id
    // firstName - first name
    // lastName - last name
    // college - college name
    // department - department name
    // email - email address
    public Student(long id, String firstName, String lastName,
                   String college, String department, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.college = college;
        this.department = department;
        this.email = email;
    }
}

// Vertex class for the graph. Wraps a student and its adjacency list
class Vertex {
    Student student;
    List<Vertex> neighbors;

    // Fields used for connector computation
    int dfsNum;
    int low;

    // Builds a vertex for a given student
    // student - student stored at this vertex
    public Vertex(Student student) {
        this.student = student;
        this.neighbors = new ArrayList<>();
        this.dfsNum = 0;
        this.low = 0;
    }
}

// Helper class that stores pending friendship ids while building the graph
class PendingFriendships {
    long ownerId;
    List<Long> friendIds;

    // Creates a record for friends of a vertex that are not connected yet
    // ownerId - id of the student
    // friendIds - list of ids of friends
    public PendingFriendships(long ownerId, List<Long> friendIds) {
        this.ownerId = ownerId;
        this.friendIds = friendIds;
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
    // filename - name of the input file
    // returns graph built from the file
    // throws IOException when the file cannot be read
    public static Graph fromFile(String filename) throws IOException {
        Graph graph = new Graph();

        List<PendingFriendships> pending = new ArrayList<>();

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

                if (tokens.length < 7) {
                    continue;
                }

                long id = Long.parseLong(tokens[0].trim());
                String firstName = tokens[1].trim();
                String lastName = tokens[2].trim();
                String college = tokens[3].trim();
                String department = tokens[4].trim();
                String email = tokens[5].trim();
                int friendCount = Integer.parseInt(tokens[6].trim());

                // Remove outer quotes from college if they exist
                if (college.length() > 1 && college.charAt(0) == '"'
                        && college.charAt(college.length() - 1) == '"') {
                    college = college.substring(1, college.length() - 1);
                }

                Student s = new Student(id, firstName, lastName, college, department, email);
                graph.addVertex(s);

                List<Long> friends = new ArrayList<>();
                for (int i = 0; i < friendCount; i++) {
                    int idx = 7 + i;
                    if (idx < tokens.length) {
                        String f = tokens[idx].trim();
                        if (!f.isEmpty()) {
                            long friendId = Long.parseLong(f);
                            friends.add(friendId);
                        }
                    }
                }
                pending.add(new PendingFriendships(id, friends));
            }
        }

        // Second pass: add undirected edges
        for (PendingFriendships pf : pending) {
            for (Long friendId : pf.friendIds) {
                // Only add each undirected edge once
                long a = pf.ownerId;
                long b = friendId;
                if (a == b) {
                    continue;
                }
                if (a < b) {
                    graph.addFriendshipById(a, b);
                }
            }
        }

        return graph;
    }

    // Adds a vertex for the given student if it does not already exist
    // student - student to add
    public void addVertex(Student student) {
        if (!vertices.containsKey(student.id)) {
            vertices.put(student.id, new Vertex(student));
        }
    }

    // Adds an undirected friendship between two students given their ids
    // If any id is missing, nothing is changed
    // id1 - id of first student
    // id2 - id of second student
    public void addFriendshipById(long id1, long id2) {
        Vertex v1 = vertices.get(id1);
        Vertex v2 = vertices.get(id2);

        if (v1 == null || v2 == null) {
            return;
        }

        addFriendship(v1, v2);
    }

    // Adds an undirected edge between two vertices if it does not already exist
    // v1 - first vertex
    // v2 - second vertex
    private void addFriendship(Vertex v1, Vertex v2) {
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
    // v1 - first vertex
    // v2 - second vertex
    // returns true if an edge was removed, false otherwise
    public boolean removeFriendship(Vertex v1, Vertex v2) {
        boolean removed1 = v1.neighbors.remove(v2);
        boolean removed2 = v2.neighbors.remove(v1);

        if (removed1 && removed2) {
            edgeCount--;
            return true;
        }
        // If lists were not symmetric, repair them lightly
        if (removed1 ^ removed2) {
            if (removed1) {
                // ensure no stale reference
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

        vertices.remove(v.student.id);
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

    // Finds the vertex by first name, ignoring case
    // firstName - first name of the student
    // returns vertex when found, or null when not found
    public Vertex findByFirstName(String firstName) {
        for (Vertex v : vertices.values()) {
            if (v.student.firstName.equalsIgnoreCase(firstName)) {
                return v;
            }
        }
        return null;
    }

    // Computes friend circles for a given college using connected components
    // Each friend circle is the set of vertices from that college that lie in
    // the same connected component of the full graph
    // collegeName - college name to filter on
    // returns list of friend circles (each circle is a list of vertices)
    public List<List<Vertex>> getFriendCirclesForCollege(String collegeName) {
        List<List<Vertex>> circles = new ArrayList<>();

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

            // Filter this component to only students from the given college
            List<Vertex> inCollege = new ArrayList<>();
            for (Vertex v : component) {
                if (v.student.college.equalsIgnoreCase(collegeName)) {
                    inCollege.add(v);
                }
            }

            if (!inCollege.isEmpty()) {
                circles.add(inCollege);
            }
        }

        return circles;
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
        class Node {
            Vertex v;
            double d;
            Node(Vertex v, double d) {
                this.v = v;
                this.d = d;
            }
        }

        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> Double.compare(a.d, b.d));

        dist.put(start, 0.0);
        pq.add(new Node(start, 0.0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (current.d > dist.get(current.v)) {
                continue;
            }

            for (Vertex neighbor : current.v.neighbors) {
                double newDist = current.d + 1.0;
                if (newDist < dist.get(neighbor)) {
                    dist.put(neighbor, newDist);
                    pq.add(new Node(neighbor, newDist));
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