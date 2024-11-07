import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class GraphParserTest {
    private GraphParser parser;

    @BeforeEach
    public void setup() {
        parser = new GraphParser();
    }

    @Test
    public void testParseGraph() throws IOException {
        String inputFilePath = "src/main/resources/input.dot";
        parser.parseGraph(inputFilePath);

        String expected = Files.readString(Paths.get("src/main/resources/expected_output.txt")).replace("\r", "");;
        //String actualOutput = parser.getGraphDetails().replace("\r\n", "\n");

        assertEquals(expected, parser.getGraphDetails());
    }

    @Test
    public void testAddNode() throws GraphParser.DuplicateNodeException {
        try {
            // Initialize or parse the graph first
            parser.parseGraph("src/main/resources/input.dot");  // Use an actual input file to parse
        } catch (IOException e) {
            e.printStackTrace();  // Handle the exception or print the stack trace
        }

        // Add nodes after graph is ready
        parser.addNode("G");
        parser.addNode("T");

        // Check node addition logic
        assertEquals(5, parser.getNodeCount());

        // Testing duplicate node exception
        assertThrows(GraphParser.DuplicateNodeException.class, () -> {
            parser.addNode("G");
        }, "Duplicate node G should throw exception.");
    }


    @Test
    public void testAddEdge() throws GraphParser.DuplicateNodeException, GraphParser.DuplicateEdgeException, IOException {
        // Initialize or parse the graph first
        parser.parseGraph("src/main/resources/input.dot");

        parser.addNode("Z");
        parser.addNode("X");
        parser.addEdge("X", "Z");

        // Check edge count after adding one edge
        assertEquals(4, parser.getEdgeCount(), "There should be 4 edge after adding X -> Z");

        // Check duplicate edge exception
        assertThrows(GraphParser.DuplicateEdgeException.class, () -> {
            parser.addEdge("X", "Z");
        }, "Duplicate edge X -> Z should throw an exception.");
    }


    @Test
    public void testOutputDOTGraph() throws IOException {
        parser.parseGraph("src/main/resources/input.dot");
        parser.outputGraph("src/main/resources/test_output.dot");

        String expectedOutput = Files.readString(Paths.get("src/main/resources/expected_output.dot")).replace("\r", "");;
        String actualOutput = Files.readString(Paths.get("src/main/resources/test_output.dot")).replace("\r", "");;

        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void testOutputGraphAsPng() throws IOException {
        parser.parseGraph("src/main/resources/input.dot");
        parser.outputGraphAsPng("src/main/resources/test_output.png");

        File outputFile = new File("src/main/resources/test_output.png");
        assertTrue(outputFile.exists(), "src/main/resources/output.png");
    }

    // Part 2 starts here:
    @Test
    public void testRemoveNode() throws IOException, GraphParser.DuplicateNodeException, GraphParser.NodeNotFoundException, GraphParser.DuplicateEdgeException {
        // Initialize the graph by parsing an input file
        parser.parseGraph("src/main/resources/input.dot");

        // Adding unique nodes "P" and "O" and an edge between them for the test
        parser.addNode("P");
        parser.addNode("O");
        parser.addEdge("P", "O");

        // Scenario 1: Successfully remove a node and verify it's gone
        parser.removeNode("P");
        assertEquals(4, parser.getNodeCount(), "Node count should be 4 after removing 'P'.");
        assertEquals(3, parser.getEdgeCount(), "Edge count should be 4 after removing node 'P' and its connected edges.");

        // Scenario 2: Try removing a node that does not exist
        assertThrows(GraphParser.NodeNotFoundException.class, () -> {
            parser.removeNode("Q");
        }, "Attempting to remove a non-existent node 'C' should throw NodeNotFoundException.");

        // Scenario 3: Removing nodes that are isolated (not connected by edges)
        parser.addNode("D");
        parser.removeNode("D");
        assertEquals(4, parser.getNodeCount(), "Node count should be 1 after removing isolated node 'D'.");
    }


    @Test
    public void testRemoveNodes() throws IOException, GraphParser.DuplicateNodeException, GraphParser.NodeNotFoundException, GraphParser.DuplicateEdgeException {
        // Initialize the graph and add unique nodes and edges for this test
        parser.parseGraph("src/main/resources/input.dot");

        // Adding unique nodes to the graph
        parser.addNode("U1");
        parser.addNode("U2");
        parser.addNode("U3");
        parser.addEdge("U1", "U2");
        parser.addEdge("U2", "U3");

        // Verify initial node and edge count
        assertEquals(6, parser.getNodeCount(), "Initial node count should be 3.");
        assertEquals(5, parser.getEdgeCount(), "Initial edge count should be 2.");

        // Scenario 1: Successfully remove multiple nodes and verify they’re gone
        String[] nodesToRemove = {"U1", "U2"};
        parser.removeNodes(nodesToRemove);
        assertEquals(4, parser.getNodeCount(), "Node count should be 1 after removing 'U1' and 'U2'.");
        assertEquals(3, parser.getEdgeCount(), "Edge count should be 3 after removing nodes 'U1' and 'U2'.");

        // Scenario 2: Try removing nodes that do not exist
        assertThrows(GraphParser.NodeNotFoundException.class, () -> {
            parser.removeNodes(new String[]{"NonExistentNode1", "NonExistentNode2"});
        }, "Attempting to remove non-existent nodes should throw NodeNotFoundException.");

        // Scenario 3: Remove an isolated node (not connected by edges)
        parser.addNode("U4");  // Add an isolated node
        assertEquals(5, parser.getNodeCount(), "Node count should be 2 after adding 'U4'.");
        parser.removeNode("U4");
        assertEquals(4, parser.getNodeCount(), "Node count should be 1 after removing isolated node 'U4'.");
    }


//    @Test
//    public void testRemoveEdges() throws Exception {
//        // Step 1: Parse and set up initial graph with nodes and edges
//        parser.parseGraph("src/main/resources/input.dot");
//        parser.addNode("U1");
//        parser.addNode("U2");
//        parser.addNode("U3");
//        parser.addEdge("U1", "U2");
//        parser.addEdge("U2", "U3");
//
//        // Scenario 1: Successfully remove existing nodes and edges
//        parser.removeEdge("U1", "U2");
//        assertEquals(4, parser.getEdgeCount(), "Edge count should be 1 after removing one edge.");
//        parser.removeNode("U1");
//        assertEquals(5, parser.getNodeCount(), "Node count should be 2 after removing one node.");
//
//        // Scenario 2: Attempt to remove a non-existent node
//        Exception nodeException = assertThrows(GraphParser.NodeNotFoundException.class, () -> {
//            parser.removeNode("NonExistentNode");
//        });
//        assertTrue(nodeException.getMessage().contains("does not exist"), "Should throw exception for non-existent node.");
//
//        // Scenario 3: Attempt to remove a non-existent edge
//        Exception edgeException = assertThrows(GraphParser.NodeNotFoundException.class, () -> {
//            parser.removeEdge("U1", "U3");
//        });
//        assertTrue(edgeException.getMessage().contains("does not exist"), "Should throw exception for non-existent edge.");
//
//        // Scenario 1 (continued): Successfully remove multiple nodes, including an existing and non-existent node
//        parser.removeNodes(new String[]{"U2", "NonExistentNode"});
//        assertEquals(1, parser.getNodeCount(), "Only existing nodes should be removed.");
//    }

    @Test
    public void testRemoveEdges() throws Exception {
        GraphParser parser = new GraphParser();

        // Step 1: Parse and set up initial graph with nodes and edges
        parser.parseGraph("src/main/resources/input.dot");

        // Add additional nodes and edges for testing
        parser.addNode("U1");
        parser.addNode("U2");
        parser.addNode("U3");
        parser.addEdge("U1", "U2");
        parser.addEdge("U2", "U3");
        parser.addEdge("U3", "U1"); // Add a cycle among new nodes for complexity

        // Check initial state
        assertEquals(6, parser.getNodeCount(), "Graph should have 6 nodes after additions.");
        assertEquals(6, parser.getEdgeCount(), "Graph should have 6 edges after additions.");

        // Scenario 1: Successfully remove an existing edge
        parser.removeEdge("U1", "U2");
        assertEquals(5, parser.getEdgeCount(), "Graph should have 5 edges after removing one.");

        // Remove another edge
        parser.removeEdge("U2", "U3");
        assertEquals(4, parser.getEdgeCount(), "Graph should have 4 edges after removing another.");

//        // Scenario 2: Attempt to remove a non-existent edge
//        Exception edgeException = assertThrows(GraphParser.NodeNotFoundException.class, () -> {
//            parser.removeEdge("U1", "U2"); // This edge was already removed
//        });
//        assertTrue(edgeException.getMessage().contains("does not exist"), "Should throw exception for non-existent edge.");

        // Scenario 3: Remove a node and verify edge and node counts
        parser.removeNode("U1");
        assertEquals(6, parser.getNodeCount(), "Graph should have 6 nodes after removing one node.");
        assertEquals(4, parser.getEdgeCount(), "Graph should have 4 edges after removing a node with edges.");


    }








}
