import guru.nidi.graphviz.model.Factory;
import guru.nidi.graphviz.model.LinkTarget;
import guru.nidi.graphviz.model.MutableNode;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Node;



import java.io.File;
import java.io.IOException;
import java.util.*;

public class GraphParser {
    private MutableGraph graph;
    private Map<String, MutableNode> nodeMap = new HashMap<>(); // To store nodes for quick access
    private Map<String, List<String>> adjacencyMap = new HashMap<>();


    // Custom exception for duplicate nodes
    public static class DuplicateNodeException extends Exception {
        public DuplicateNodeException(String message) {
            super(message);
        }
    }

    // Custom exception for duplicate edges
    public static class DuplicateEdgeException extends Exception {
        public DuplicateEdgeException(String message) {
            super(message);
        }
    }

    //Part 2 - exception for non-existent nodes
    public static class NodeNotFoundException extends Exception {
        public NodeNotFoundException(String message) {
            super(message);
        }
    }

    // Feature 1: Parses a DOT file and creates a graph
    public void parseGraph(String filepath) throws IOException {
        this.graph = new Parser().read(new File(filepath));
        System.out.println("Graph parsed successfully.");
        updateNodeMap();
    }

    // Output the graph details like number of nodes and edges
    public void printGraphDetails() {
        if (graph != null) {
            System.out.println("Number of nodes: " + graph.nodes().size());
            graph.nodes().forEach(node -> System.out.println("Node label: " + node.name()));
            System.out.println("Number of edges: " + graph.edges().size());
            graph.edges().forEach(edge -> System.out.println("Edge from " + edge.from().name() + " to " + edge.to().name()));
        } else {
            System.out.println("Graph is not initialized.");
        }
    }

    // Feature 2: Add a single node to the graph, throwing an exception if it already exists
    public void addNode(String label) throws DuplicateNodeException {
        if (graph != null) {
            boolean exists = graph.nodes().stream().anyMatch(node -> node.name().toString().equals(label));
            if (exists) {
                throw new DuplicateNodeException("Node " + label + " already exists.");
            } else {
                MutableNode newNode = Factory.mutNode(label);
                graph.add(newNode);
                nodeMap.put(label, newNode); // Add to nodeMap
                System.out.println("Node " + label + " added successfully.");
            }
        } else {
            System.out.println("Graph is not initialized.");
        }
    }

    // Feature 2: Add multiple nodes to the graph
    public void addNodes(String[] labels) {
        if (graph != null) {
            for (String label : labels) {
                try {
                    addNode(label);
                } catch (DuplicateNodeException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else {
            System.out.println("Graph is not initialized.");
        }
    }

    // Feature 3: Add an edge and check for duplicates
    public void addEdge(String srcLabel, String dstLabel) throws DuplicateEdgeException {
        if (graph != null) {
            MutableNode srcNode = null;
            MutableNode dstNode = null;

            // Find source and destination nodes
            for (MutableNode node : graph.nodes()) {
                if (node.name().toString().equals(srcLabel)) {
                    srcNode = node;
                }
                if (node.name().toString().equals(dstLabel)) {
                    dstNode = node;
                }
            }

            if (srcNode == null || dstNode == null) {
                System.out.println("Source or destination node does not exist.");
                return;
            }

            // Check if the edge already exists
            boolean exists = graph.edges().stream().anyMatch(edge ->
                    edge.from().name().toString().equals(srcLabel) &&
                            edge.to().name().toString().equals(dstLabel)
            );

            if (exists) {
                throw new DuplicateEdgeException("Edge from " + srcLabel + " to " + dstLabel + " already exists.");
            } else {
                srcNode.addLink(dstNode);
                System.out.println("Edge from " + srcLabel + " to " + dstLabel + " added successfully.");
                // Update the adjacency map
                adjacencyMap.computeIfAbsent(srcLabel, k -> new ArrayList<>()).add(dstLabel);
            }
        } else {
            System.out.println("Graph is not initialized.");
        }
    }

    // Save the graph to a DOT file
    public void outputGraph(String filepath) throws IOException {
        if (graph != null) {
            Graphviz.fromGraph(graph).render(Format.DOT).toFile(new File(filepath));
            System.out.println("Graph saved to " + filepath);
        } else {
            System.out.println("Graph is not initialized.");
        }
    }

    // Save the graph to a PNG file
    public void outputGraphAsPng(String filepath) throws IOException {
        if (graph != null) {
            Graphviz.fromGraph(graph).width(500).render(Format.PNG).toFile(new File(filepath));
            System.out.println("Graph image saved to " + filepath);
        } else {
            System.out.println("Graph is not initialized.");
        }
    }

    // junit task
    public String getGraphDetails() {
        StringBuilder builder = new StringBuilder();
        if (graph != null) {
            builder.append("Number of nodes: ").append(graph.nodes().size()).append("\n");
            graph.nodes().forEach(node -> builder.append("Node label: ").append(node.name()).append("\n"));
            builder.append("Number of edges: ").append(graph.edges().size()).append("\n");
            graph.edges().forEach(edge -> builder.append("Edge from ").append(edge.from().name())
                    .append(" to ").append(edge.to().name()).append("\n"));
        } else {
            builder.append("Graph is not initialized.");
        }
        return builder.toString();
    }


    public int getNodeCount() {
        if (graph != null) {
            return graph.nodes().size();
        } else {
            return 0;
        }
    }

    public int getEdgeCount() {
        if (graph != null) {
            return graph.edges().size();
        } else {
            return 0;
        }
    }


    // Part 2 of the project starts here:

    // populate nodeMap from graph
    private void updateNodeMap() {
        nodeMap.clear();
        graph.nodes().forEach(node -> nodeMap.put(node.name().toString(), node));
    }


    // Method to remove a single node
    public void removeNode(String label) throws NodeNotFoundException {
        MutableNode nodeToRemove = nodeMap.get(label);

        if (nodeToRemove == null) {
            throw new NodeNotFoundException("Node " + label + " does not exist.");
        }

        // Remove all edges connected to the node
        graph.edges().removeIf(edge -> edge.from().name().toString().equals(label) || edge.to().name().toString().equals(label));

        // Remove the node itself by creating a new set of nodes excluding the removed node
        MutableGraph updatedGraph = Factory.mutGraph("G").setDirected(true);
        graph.nodes().forEach(node -> {
            if (!node.name().equals(nodeToRemove.name())) {
                updatedGraph.add(node);
            }
        });

        // Update the graph and nodeMap
        this.graph = updatedGraph;
        nodeMap.remove(label);

        System.out.println("Node " + label + " removed successfully.");
        updateNodeMap(); // Update node map to reflect the current graph state
    }


    // Method to remove multiple nodes
    public void removeNodes(String[] labels) throws NodeNotFoundException {
        StringBuilder missingNodes = new StringBuilder();

        for (String label : labels) {
            try {
                removeNode(label); // Attempt to remove each node
            } catch (NodeNotFoundException e) {
                missingNodes.append(label).append(" "); // Collect missing nodes
                System.out.println(e.getMessage()); // Print message for each missing node
            }
        }

        // After attempting all deletions, check if any nodes were missing
        if (missingNodes.length() > 0) {
            throw new NodeNotFoundException("The following nodes do not exist: " + missingNodes.toString().trim());
        }

        System.out.println("All specified nodes removed successfully, if they existed.");
    }


    // Method to remove an edge
    public void removeEdge(String srcLabel, String dstLabel) throws NodeNotFoundException {
        if (!nodeMap.containsKey(srcLabel) || !nodeMap.containsKey(dstLabel)) {
            throw new NodeNotFoundException("One or both nodes do not exist: " + srcLabel + ", " + dstLabel);
        }

        // Create a new graph object to ensure all edges are properly reset
        MutableGraph updatedGraph = Factory.mutGraph("G").setDirected(true);
        Map<String, MutableNode> updatedNodeMap = new HashMap<>();

        // Re-create all nodes in the updated graph and map
        for (String label : nodeMap.keySet()) {
            MutableNode newNode = Factory.mutNode(label);
            updatedGraph.add(newNode);
            updatedNodeMap.put(label, newNode);
        }

        // Re-add links, excluding the specified edge from srcLabel to dstLabel
        for (Map.Entry<String, MutableNode> entry : nodeMap.entrySet()) {
            String nodeName = entry.getKey();
            MutableNode node = entry.getValue();
            MutableNode updatedNode = updatedNodeMap.get(nodeName);

            node.links().forEach(link -> {
                String targetName = link.to().name().toString();
                if (!(nodeName.equals(srcLabel) && targetName.equals(dstLabel))) {
                    MutableNode targetNode = updatedNodeMap.get(targetName);
                    if (targetNode != null) {
                        updatedNode.addLink(targetNode);
                    }
                }
            });
        }

        // Replace the main graph and node map
        this.graph = updatedGraph;
        this.nodeMap = updatedNodeMap;

        System.out.println("Edge from " + srcLabel + " to " + dstLabel + " removed successfully.");
    }




    public Path GraphSearch(String srcLabel, String dstLabel, Algorithm algo) throws NodeNotFoundException {
        if (!nodeMap.containsKey(srcLabel) || !nodeMap.containsKey(dstLabel)) {
            throw new NodeNotFoundException("One or both nodes do not exist: " + srcLabel + ", " + dstLabel);
        }

        GraphTraversal traversal;

        // Dynamically select BFS or DFS
        if (algo == Algorithm.BFS) {
            traversal = new BFSTraversal(adjacencyMap);
        } else if (algo == Algorithm.DFS) {
            traversal = new DFSTraversal(adjacencyMap);
        } else {
            throw new IllegalArgumentException("Unsupported algorithm: " + algo);
        }

        return traversal.search(srcLabel, dstLabel); // Perform the traversal
    }



    // graph search bfs
    private Path GraphSearchBFS(String srcLabel, String dstLabel) throws NodeNotFoundException {
        srcLabel = srcLabel;
        dstLabel = dstLabel;

        if (!nodeMap.containsKey(srcLabel) || !nodeMap.containsKey(dstLabel)) {
            throw new NodeNotFoundException("One or both nodes do not exist: " + srcLabel + ", " + dstLabel);
        }

        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        // Start BFS from the source node
        List<String> startPath = new ArrayList<>();
        startPath.add(srcLabel);
        queue.add(startPath);
        visited.add(srcLabel);

        while (!queue.isEmpty()) {
            List<String> currentPath = queue.poll();
            String currentNode = currentPath.get(currentPath.size() - 1);

            System.out.println("Current path: " + currentPath);
            System.out.println("Visiting node: " + currentNode);

            if (currentNode.equals(dstLabel)) {
                Path path = new Path();
                for (String node : currentPath) {
                    path.addNode(node);
                }
                return path; // Return the path if destination is found
            }

            List<String> neighbors = adjacencyMap.getOrDefault(currentNode, new ArrayList<>());
            for (String neighbor : neighbors) {
                System.out.println("Checking neighbor: " + neighbor); // Debugging neighbor

                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);

                    // Create a new path to the neighbor
                    List<String> newPath = new ArrayList<>(currentPath);
                    newPath.add(neighbor);
                    queue.add(newPath);
                }
            }
        }

        return null; // Return null if no path is found
    }


    //  graph search dfs
    private Path GraphSearchDFS(String srcLabel, String dstLabel) {
        // Check if both source and destination nodes exist in the graph
        if (!nodeMap.containsKey(srcLabel) || !nodeMap.containsKey(dstLabel)) {
            System.out.println("One or both nodes do not exist.");
            return null;
        }

        // Initialize stack and visited set
        Stack<List<String>> stack = new Stack<>();
        Set<String> visited = new HashSet<>();

        // Initialize the path with the source node
        List<String> initialPath = new ArrayList<>();
        initialPath.add(srcLabel);
        stack.push(initialPath);

        // Perform DFS
        while (!stack.isEmpty()) {
            List<String> currentPath = stack.pop();
            String currentNodeLabel = currentPath.get(currentPath.size() - 1);

            System.out.println("Current path: " + currentPath); // Debug
            System.out.println("Visiting node: " + currentNodeLabel); // Debug

            // Check if the current node is the destination
            if (currentNodeLabel.equals(dstLabel)) {
                Path path = new Path();
                for (String node : currentPath) {
                    path.addNode(node);
                }
                System.out.println("Path found (DFS): " + path);
                return path;
            }

            // Mark the node as visited
            if (!visited.contains(currentNodeLabel)) {
                visited.add(currentNodeLabel);

                // Retrieve neighbors from adjacencyMap
                List<String> neighbors = adjacencyMap.getOrDefault(currentNodeLabel, new ArrayList<>());
                for (String neighbor : neighbors) {
                    System.out.println("Checking neighbor: " + neighbor); // Debug

                    if (!visited.contains(neighbor)) {
                        // Create a new path to the neighbor
                        List<String> newPath = new ArrayList<>(currentPath);
                        newPath.add(neighbor);
                        stack.push(newPath);
                    }
                }
            }
        }

        // No path found
        System.out.println("No path found from " + srcLabel + " to " + dstLabel);
        return null;
    }



    public Map<String, List<String>> getAdjacencyMap() {
        return adjacencyMap;
    }


    public static void main(String[] args) throws DuplicateEdgeException, NodeNotFoundException, IOException {
        GraphParser parser = new GraphParser();
        Scanner scanner = new Scanner(System.in);

        parser.parseGraph("src/main/resources/input.dot");
        parser.printGraphDetails();

        int choice;

        do {
            System.out.println("Choose an option:");
            System.out.println("1 - Add Node");
            System.out.println("2 - Add Edge");
            System.out.println("3 - Remove Node");
            System.out.println("4 - Remove Edge");
            System.out.println("5 - Find Path Between Nodes");
            System.out.println("6 - Exit");
            choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1: // Add Node
                    System.out.println("Enter node labels separated by spaces:");
                    String[] nodes = scanner.nextLine().split(" ");
                    parser.addNodes(nodes);
                    break;

                case 2: // Add Edge
                    System.out.println("Enter the source and destination node labels (format: src dst):");
                    String[] edge = scanner.nextLine().split(" ");
                    if (edge.length == 2) {
                        parser.addEdge(edge[0], edge[1]);
                    } else {
                        System.out.println("Invalid input. Please enter two node labels.");
                    }
                    break;

                case 3: // Remove Node
                    System.out.println("Enter the labels of the nodes to remove, separated by spaces:");
                    String[] nodesToRemove = scanner.nextLine().split(" ");
                    parser.removeNodes(nodesToRemove);
                    break;

                case 4: // Remove Edge
                    System.out.println("Enter the source and destination node labels for the edge to remove (format: src dst):");
                    String[] edgeToRemove = scanner.nextLine().split(" ");
                    if (edgeToRemove.length == 2) {
                        parser.removeEdge(edgeToRemove[0], edgeToRemove[1]);
                    } else {
                        System.out.println("Invalid input. Please enter two node labels.");
                    }
                    break;

                case 5: // Find Path Between Nodes
                    System.out.println("Enter the source and destination node labels for path search (format: src dst):");
                    String[] searchNodes = scanner.nextLine().split(" ");
                    if (searchNodes.length == 2) {
                        System.out.println("Select search algorithm: (1 - BFS, 2 - DFS)");
                        int algorithmChoice = Integer.parseInt(scanner.nextLine());
                        Algorithm algorithm = (algorithmChoice == 1) ? Algorithm.BFS : Algorithm.DFS;

                        try {
                            // Call GraphSearch using the parser instance
                            Path path = parser.GraphSearch(searchNodes[0], searchNodes[1], algorithm);
                            if (path != null) {
                                System.out.println("Path found (" + algorithm + "): " + path);
                            } else {
                                System.out.println("No path found from " + searchNodes[0] + " to " + searchNodes[1]);
                            }
                        } catch (NodeNotFoundException e) {
                            System.out.println(e.getMessage());
                        }
                    } else {
                        System.out.println("Invalid input. Please enter two node labels.");
                    }
                    break;



                case 6: // Exit
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 6.");
            }

            // Optional: display the updated graph details after each operation
            parser.printGraphDetails();

        } while (choice != 6);

        // Output final graph to a file
        parser.outputGraph("src/main/resources/output.dot");
        parser.outputGraphAsPng("src/main/resources/output.png");
    }



}

