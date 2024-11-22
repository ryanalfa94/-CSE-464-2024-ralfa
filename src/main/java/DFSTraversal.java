import java.util.*;

public class DFSTraversal extends GraphTraversal {
    private Stack<String> stack;
    private Map<String, String> parentMap;

    public DFSTraversal(Map<String, List<String>> adjacencyMap) {
        super(adjacencyMap);
    }

    @Override
    protected void initializeTraversal(String srcLabel) {
        stack = new Stack<>();
        parentMap = new HashMap<>();
        stack.push(srcLabel);
        parentMap.put(srcLabel, null); // Source node has no parent
        System.out.println("Initialized DFS with source: " + srcLabel);
    }

    @Override
    protected boolean isTraversalComplete() {
        System.out.println("Stack status: " + stack);
        return stack.isEmpty();
    }

    @Override
    protected String getNextNode() {
        String nextNode = stack.pop();
        System.out.println("Popped node: " + nextNode);
        return nextNode;
    }

    @Override
    protected void addNodeToTraversal(String neighbor, String currentNode) {
        stack.push(neighbor);
        parentMap.put(neighbor, currentNode);
        System.out.println("Pushed neighbor: " + neighbor + ", Parent: " + currentNode);
    }

    @Override
    protected Path constructPath(String dstLabel) {
        Path path = new Path();
        String currentNode = dstLabel;

        System.out.println("Reconstructing DFS path...");
        while (currentNode != null) {
            System.out.println("Adding node to path: " + currentNode);
            path.addNode(currentNode);
            currentNode = parentMap.get(currentNode); // Trace back using parent map
        }

        Collections.reverse(path.getNodes());
        System.out.println("Constructed DFS path: " + path);
        return path;
    }
}
