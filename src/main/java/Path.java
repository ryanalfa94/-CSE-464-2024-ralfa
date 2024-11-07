import java.util.ArrayList;
import java.util.List;

public class Path {
    private List<String> nodes; // Stores node labels as a path

    public Path() {
        this.nodes = new ArrayList<>();
    }

    // Add a node to the path
    public void addNode(String nodeLabel) {
        nodes.add(nodeLabel);
    }

    // Get all nodes in the path
    public List<String> getNodes() {
        return nodes;
    }

    // Represent the path as a string
    @Override
    public String toString() {
        return String.join(" -> ", nodes);
    }
}
