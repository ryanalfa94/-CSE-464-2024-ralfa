import java.util.ArrayList;
import java.util.List;
//
//public class Path {
//    private List<String> nodes; // Stores node labels as a path
//
//    public Path() {
//        this.nodes = new ArrayList<>();
//    }
//
//    // New constructor to initialize the path with the starting node
//    public Path(String initialNode) {
//        this.nodes = new ArrayList<>();
//        nodes.add(initialNode);
//    }
//
//    // Add a node to the path
//    public void addNode(String nodeLabel) {
//        nodes.add(nodeLabel);
//    }
//
//    // Get all nodes in the path
//    public List<String> getNodes() {
//        return nodes;
//    }
//
//    public String getLastNode() {
//        if (nodes.isEmpty()) {
//            return null;
//        }
//        return nodes.get(nodes.size() - 1);
//    }
//
//    // Represent the path as a string
//    @Override
//    public String toString() {
//        return String.join(" -> ", nodes);
//    }
//}




public class Path {
    private List<String> nodes;

    public Path() {
        this.nodes = new ArrayList<>();
    }

    // Copy constructor
    public Path(Path other) {
        this.nodes = new ArrayList<>(other.nodes);
    }

    public void addNode(String nodeLabel) {
        nodes.add(nodeLabel);
    }

    public List<String> getNodes() {
        return nodes;
    }

    public String getLastNode() {
        return nodes.isEmpty() ? null : nodes.get(nodes.size() - 1);
    }

    @Override
    public String toString() {
        return String.join(" -> ", nodes);
    }
}

