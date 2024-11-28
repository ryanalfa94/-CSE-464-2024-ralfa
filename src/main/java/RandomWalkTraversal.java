import java.util.*;

public class RandomWalkTraversal extends GraphTraversal implements GraphSearchStrategy {
    private Random random;

    public RandomWalkTraversal(Map<String, List<String>> adjacencyMap) {
        super(adjacencyMap);
        this.random = new Random();
    }

    @Override
    protected void initializeTraversal(String srcLabel) {
        // Initialize the search with the starting node
        traversalQueue.add(srcLabel); // Add the starting node to the queue
        parentMap.put(srcLabel, null); // Initialize parent map
    }

    @Override
    protected boolean isTraversalComplete() {
        // The traversal is complete when there are no more nodes to visit
        return traversalQueue.isEmpty();
    }

//    @Override
//    protected String getNextNode() {
//        // Poll the current node from the queue
//        String currentNode = traversalQueue.poll();
//        if (currentNode == null) return null;
//
//        // Randomly select the next neighbor to visit
//        List<String> neighbors = adjacencyMap.getOrDefault(currentNode, new ArrayList<>());
//
//        if (!neighbors.isEmpty()) {
//            // Choose a random neighbor
//            String nextNode = neighbors.get(random.nextInt(neighbors.size()));
//            return nextNode;
//        }
//
//        return null; // No valid neighbors found
//    }

    @Override
    protected String getNextNode() {
        String currentNode = traversalQueue.poll();
        if (currentNode == null) return null;

        List<String> neighbors = adjacencyMap.getOrDefault(currentNode, new ArrayList<>());
        if (!neighbors.isEmpty()) {
            // Randomly select the next neighbor
            String nextNode = neighbors.get(random.nextInt(neighbors.size()));
            if (!parentMap.containsKey(nextNode)) { // Avoid revisiting nodes
                traversalQueue.add(nextNode);
                parentMap.put(nextNode, currentNode); // Link the next node to its parent
                System.out.println("Randomly selected next node: " + nextNode + " (Parent: " + currentNode + ")");
            }
            return nextNode;
        }

        return null; // No neighbors to visit
    }


//    @Override
//    protected void addNodeToTraversal(String neighbor, String currentNode) {
//        // Add the neighbor to the traversal queue
//        if (!parentMap.containsKey(neighbor)) { // Avoid re-adding visited nodes
//            traversalQueue.add(neighbor);
//            parentMap.put(neighbor, currentNode); // Track the parent for path reconstruction
//        }
//    }

    @Override
    protected void addNodeToTraversal(String neighbor, String currentNode) {
        // Add the neighbor to the traversal queue
        if (!parentMap.containsKey(neighbor)) { // Avoid re-adding visited nodes
            traversalQueue.add(neighbor);
            parentMap.put(neighbor, currentNode); // Track the parent for path reconstruction
            System.out.println("Added to traversal: " + neighbor + " (Parent: " + currentNode + ")");
            System.out.println("Current parentMap: " + parentMap);
        }
    }


//    @Override
//    protected Path constructPath(String dstLabel) {
//        // Reconstruct the path from destination to source
//        Path path = new Path();
//        String current = dstLabel;
//
//        while (current != null) {
//            path.addNode(current);
//            current = parentMap.get(current); // Walk back through the parent map
//        }
//
//        Collections.reverse(path.getNodes()); // Reverse to get the correct order
//        return path;
//    }


    @Override
    protected Path constructPath(String dstLabel) {
        System.out.println("Reconstructing path to destination: " + dstLabel);
        Path path = new Path();
        String current = dstLabel;

        while (current != null) {
            path.addNode(current);
            current = parentMap.get(current); // Walk back through the parent map
        }

        Collections.reverse(path.getNodes()); // Reverse to get the correct order
        System.out.println("Constructed Path: " + path);
        return path;
    }


    @Override
    public Path search(String src, String dst) {
        // Override the search method to handle Random Walk-specific logic
        System.out.println("Starting Random Walk from " + src + " to " + dst);
        return super.search(src, dst);
    }
}
