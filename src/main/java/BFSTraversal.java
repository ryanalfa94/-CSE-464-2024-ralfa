//import java.util.*;
//
//public class BFSTraversal extends GraphTraversal {
//    private Queue<String> queue;
//    private Map<String, String> parentMap;
//
//    public BFSTraversal(Map<String, List<String>> adjacencyMap) {
//        super(adjacencyMap);
//    }
//
//    @Override
//    protected void initializeTraversal(String srcLabel) {
//        queue = new LinkedList<>();
//        parentMap = new HashMap<>();
//        queue.add(srcLabel);
//        parentMap.put(srcLabel, null); // Source node has no parent
//        System.out.println("Initialized BFS with source: " + srcLabel);
//    }
//
//    @Override
//    protected boolean isTraversalComplete() {
//        System.out.println("Queue status: " + queue);
//        return queue.isEmpty();
//    }
//
//    @Override
//    protected String getNextNode() {
//        String nextNode = queue.poll();
//        System.out.println("Dequeued node: " + nextNode);
//        return nextNode;
//    }
//
//    @Override
//    protected void addNodeToTraversal(String neighbor, String currentNode) {
//        queue.add(neighbor);
//        parentMap.put(neighbor, currentNode);
//        System.out.println("Enqueued neighbor: " + neighbor + ", Parent: " + currentNode);
//    }
//
//    @Override
//    protected Path constructPath(String dstLabel) {
//        Path path = new Path();
//        String currentNode = dstLabel;
//
//        System.out.println("Reconstructing BFS path...");
//        while (currentNode != null) {
//            System.out.println("Adding node to path: " + currentNode);
//            path.addNode(currentNode);
//            currentNode = parentMap.get(currentNode); // Trace back using parent map
//        }
//
//        Collections.reverse(path.getNodes());
//        System.out.println("Constructed BFS path: " + path);
//        return path;
//    }
//}
// I was not sure if we are creating a new refactor branch or the same one so i comment everything just in case
// i need to go back to it


import java.util.*;

public class BFSTraversal implements GraphSearchStrategy {
    private Map<String, List<String>> adjacencyMap;

    public BFSTraversal(Map<String, List<String>> adjacencyMap) {
        this.adjacencyMap = adjacencyMap;
    }

    @Override
    public Path search(String src, String dst) {
        Queue<String> queue = new LinkedList<>();
        Map<String, String> parentMap = new HashMap<>();
        Set<String> visited = new HashSet<>();

        queue.add(src);
        parentMap.put(src, null);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            visited.add(current);

            if (current.equals(dst)) {
                return constructPath(parentMap, dst);
            }

            for (String neighbor : adjacencyMap.getOrDefault(current, new ArrayList<>())) {
                if (!visited.contains(neighbor) && !parentMap.containsKey(neighbor)) {
                    parentMap.put(neighbor, current);
                    queue.add(neighbor);
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

