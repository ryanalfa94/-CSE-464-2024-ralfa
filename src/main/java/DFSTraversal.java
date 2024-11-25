//import java.util.*;
//
//public class DFSTraversal extends GraphTraversal {
//    private Stack<String> stack;
//    private Map<String, String> parentMap;
//
//    public DFSTraversal(Map<String, List<String>> adjacencyMap) {
//        super(adjacencyMap);
//    }
//
//    @Override
//    protected void initializeTraversal(String srcLabel) {
//        stack = new Stack<>();
//        parentMap = new HashMap<>();
//        stack.push(srcLabel);
//        parentMap.put(srcLabel, null); // Source node has no parent
//        System.out.println("Initialized DFS with source: " + srcLabel);
//    }
//
//    @Override
//    protected boolean isTraversalComplete() {
//        System.out.println("Stack status: " + stack);
//        return stack.isEmpty();
//    }
//
//    @Override
//    protected String getNextNode() {
//        String nextNode = stack.pop();
//        System.out.println("Popped node: " + nextNode);
//        return nextNode;
//    }
//
//    @Override
//    protected void addNodeToTraversal(String neighbor, String currentNode) {
//        stack.push(neighbor);
//        parentMap.put(neighbor, currentNode);
//        System.out.println("Pushed neighbor: " + neighbor + ", Parent: " + currentNode);
//    }
//
//    @Override
//    protected Path constructPath(String dstLabel) {
//        Path path = new Path();
//        String currentNode = dstLabel;
//
//        System.out.println("Reconstructing DFS path...");
//        while (currentNode != null) {
//            System.out.println("Adding node to path: " + currentNode);
//            path.addNode(currentNode);
//            currentNode = parentMap.get(currentNode); // Trace back using parent map
//        }
//
//        Collections.reverse(path.getNodes());
//        System.out.println("Constructed DFS path: " + path);
//        return path;
//    }
//} commented the whole thing for my own sanity

import java.util.*;

public class DFSTraversal implements GraphSearchStrategy {
    private Map<String, List<String>> adjacencyMap;

    public DFSTraversal(Map<String, List<String>> adjacencyMap) {
        this.adjacencyMap = adjacencyMap;
    }

    @Override
    public Path search(String src, String dst) {
        Stack<String> stack = new Stack<>();
        Map<String, String> parentMap = new HashMap<>();
        Set<String> visited = new HashSet<>();

        stack.push(src);
        parentMap.put(src, null);

        while (!stack.isEmpty()) {
            String current = stack.pop();

            if (visited.contains(current)) {
                continue;
            }

            visited.add(current);

            if (current.equals(dst)) {
                return constructPath(parentMap, dst);
            }

            for (String neighbor : adjacencyMap.getOrDefault(current, new ArrayList<>())) {
                if (!visited.contains(neighbor)) {
                    parentMap.put(neighbor, current);
                    stack.push(neighbor);
                }
            }
        }

        return null; // No path found
    }

    private Path constructPath(Map<String, String> parentMap, String dst) {
        Path path = new Path();
        String current = dst;

        while (current != null) {
            path.addNode(current);
            current = parentMap.get(current);
        }

        Collections.reverse(path.getNodes());
        return path;
    }
}

