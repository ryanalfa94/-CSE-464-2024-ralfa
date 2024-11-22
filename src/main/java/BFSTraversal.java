import java.util.*;

public class BFSTraversal extends GraphTraversal {
    private Queue<String> queue;
    private Map<String, String> parentMap;

    public BFSTraversal(Map<String, List<String>> adjacencyMap) {
        super(adjacencyMap);
    }

    @Override
    protected void initializeTraversal(String srcLabel) {
        queue = new LinkedList<>();
        parentMap = new HashMap<>();
        queue.add(srcLabel);
        parentMap.put(srcLabel, null); // Source node has no parent
        System.out.println("Initialized BFS with source: " + srcLabel);
    }

    @Override
    protected boolean isTraversalComplete() {
        System.out.println("Queue status: " + queue);
        return queue.isEmpty();
    }

    @Override
    protected String getNextNode() {
        String nextNode = queue.poll();
        System.out.println("Dequeued node: " + nextNode);
        return nextNode;
    }

    @Override
    protected void addNodeToTraversal(String neighbor, String currentNode) {
        queue.add(neighbor);
        parentMap.put(neighbor, currentNode);
        System.out.println("Enqueued neighbor: " + neighbor + ", Parent: " + currentNode);
    }

    @Override
    protected Path constructPath(String dstLabel) {
        Path path = new Path();
        String currentNode = dstLabel;

        System.out.println("Reconstructing BFS path...");
        while (currentNode != null) {
            System.out.println("Adding node to path: " + currentNode);
            path.addNode(currentNode);
            currentNode = parentMap.get(currentNode); // Trace back using parent map
        }

        Collections.reverse(path.getNodes());
        System.out.println("Constructed BFS path: " + path);
        return path;
    }
}

