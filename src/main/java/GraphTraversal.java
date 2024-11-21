import java.util.*;

public abstract class GraphTraversal {
    protected Map<String, List<String>> adjacencyMap;

    public GraphTraversal(Map<String, List<String>> adjacencyMap) {
        this.adjacencyMap = adjacencyMap;
    }

    // Template method for graph traversal
    public final Path search(String srcLabel, String dstLabel) {
        Set<String> visited = new HashSet<>();
        initializeTraversal(srcLabel);

        while (!isTraversalComplete()) {
            String currentNode = getNextNode();

            if (currentNode == null) continue; // Skip if no nodes to process
            if (visited.contains(currentNode)) continue; // Skip already visited nodes

            visited.add(currentNode);

            System.out.println("Visiting node: " + currentNode);

            if (currentNode.equals(dstLabel)) {
                System.out.println("Destination " + dstLabel + " reached. Constructing path...");
                return constructPath(dstLabel); // Construct and return the path
            }

            List<String> neighbors = adjacencyMap.getOrDefault(currentNode, new ArrayList<>());
            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    addNodeToTraversal(neighbor, currentNode);
                }
            }
        }

        System.out.println("No path found from " + srcLabel + " to " + dstLabel);
        return null; // Return null if no path is found
    }

    // Abstract methods for specific traversal behavior
    protected abstract void initializeTraversal(String srcLabel);
    protected abstract boolean isTraversalComplete();
    protected abstract String getNextNode();
    protected abstract void addNodeToTraversal(String neighbor, String currentNode);
    protected abstract Path constructPath(String dstLabel);
}

