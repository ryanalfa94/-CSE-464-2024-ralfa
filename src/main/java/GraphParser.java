import guru.nidi.graphviz.model.Factory;
import guru.nidi.graphviz.model.MutableNode;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

public class GraphParser {
    private MutableGraph graph;
    private Map<String, MutableNode> nodeMap = new HashMap<>(); // To store nodes for quick access


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

    // remove a node from the graph
    public void removeNode(String label) throws NodeNotFoundException {
        if (graph != null) {
            MutableNode nodeToRemove = nodeMap.get(label);
            if (nodeToRemove == null) {
                throw new NodeNotFoundException("Node " + label + " does not exist.");
            }

            // Remove edges connected to this node
            graph.edges().removeIf(edge -> edge.from().name().toString().equals(label) || edge.to().name().toString().equals(label));

            // Remove the node itself
            graph.nodes().remove(nodeToRemove);
            nodeMap.remove(label); // Update the nodeMap

            System.out.println("Node " + label + " removed successfully.");
        } else {
            System.out.println("Graph is not initialized.");
        }
    }



    public static void main(String[] args) {
        GraphParser parser = new GraphParser();
        try {
            parser.parseGraph("src/main/resources/input.dot");
            parser.printGraphDetails();

            Scanner scanner = new Scanner(System.in);
            String response;

            // Keep asking the user if they want to add nodes or edges until they say "no"
            do {
                System.out.println("Do you want to add nodes? (yes/no)");
                response = scanner.nextLine().toLowerCase();

                if (response.equals("yes")) {
                    System.out.println("Enter node labels separated by spaces:");
                    String[] nodes = scanner.nextLine().split(" ");
                    parser.addNodes(nodes);
                }

                System.out.println("Do you want to add an edge? (yes/no)");
                response = scanner.nextLine().toLowerCase();

                if (response.equals("yes")) {
                    System.out.println("Enter the source and destination node labels (format: src dst):");
                    String[] edge = scanner.nextLine().split(" ");
                    if (edge.length == 2) {
                        try {
                            parser.addEdge(edge[0], edge[1]);
                        } catch (DuplicateEdgeException e) {
                            System.out.println(e.getMessage());
                        }
                    } else {
                        System.out.println("Invalid input. Please enter two node labels.");
                    }
                }

                // Ask if the user wants to remove nodes
                System.out.println("Do you want to remove nodes? (yes/no)");
                response = scanner.nextLine().toLowerCase();

                if (response.equals("yes")) {
                    System.out.println("Enter node labels separated by spaces:");
                    String[] nodesToRemove = scanner.nextLine().split(" ");
                    for (String nodeLabel : nodesToRemove) {
                        try {
                            parser.removeNode(nodeLabel);
                            System.out.println("Node " + nodeLabel + " removed successfully.");
                        } catch (NodeNotFoundException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }

                // After adding and removing nodes and edges, print the graph details
                parser.printGraphDetails();

            } while (!response.equals("no"));

            // Replace with your actual output path
            parser.outputGraph("src/main/resources/output.dot");
            parser.outputGraphAsPng("src/main/resources/output.png");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
