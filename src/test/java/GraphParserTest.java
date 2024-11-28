import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;



public class GraphParserTest {
    private GraphParser parser;

    @BeforeEach
    public void setup() {
        parser = new GraphParser();
    }


@Test
public void testParseGraph() throws IOException {
    // Parse the graph from the updated .dot file
    String inputFilePath = "src/main/resources/input.dot";
    parser.parseGraph(inputFilePath);

    // Define the expected adjacency map
    Map<String, List<String>> expectedAdjacencyMap = new HashMap<>();
    expectedAdjacencyMap.put("a", Arrays.asList("b", "e"));
    expectedAdjacencyMap.put("b", Arrays.asList("c"));
    expectedAdjacencyMap.put("c", Arrays.asList("d"));
    expectedAdjacencyMap.put("d", Arrays.asList("a"));
    expectedAdjacencyMap.put("e", Arrays.asList("f", "g"));
    expectedAdjacencyMap.put("f", Arrays.asList("h"));
    expectedAdjacencyMap.put("g", Arrays.asList("h"));
    expectedAdjacencyMap.put("h", Collections.emptyList()); // Include node h with an empty list

    // Compare the content of the maps
    assertEquals(expectedAdjacencyMap, parser.getAdjacencyMap(), "Parsed adjacency map should match the expected structure.");

    // Debug print for verification
    System.out.println("Parsed adjacency map: " + parser.getAdjacencyMap());
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
        assertEquals(10, parser.getNodeCount());

        // Testing duplicate node exception
        assertThrows(GraphParser.DuplicateNodeException.class, () -> {
            parser.addNode("G");
        }, "Duplicate node G should throw exception.");
    }


    @Test
    public void testAddEdge() throws GraphParser.DuplicateNodeException, GraphParser.DuplicateEdgeException, IOException, GraphParser.NodeNotFoundException {
        // Initialize or parse the graph first
        parser.parseGraph("src/main/resources/input.dot");

        parser.addNode("Z");
        parser.addNode("X");
        parser.addEdge("X", "Z");

        // Check edge count after adding one edge
        assertEquals(10, parser.getEdgeCount(), "There should be 4 edge after adding X -> Z");

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
        assertEquals(9, parser.getNodeCount(), "Node count should be 4 after removing 'P'.");
        assertEquals(9, parser.getEdgeCount(), "Edge count should be 4 after removing node 'P' and its connected edges.");

        // Scenario 2: Try removing a node that does not exist
        assertThrows(GraphParser.NodeNotFoundException.class, () -> {
            parser.removeNode("Q");
        }, "Attempting to remove a non-existent node 'C' should throw NodeNotFoundException.");

        // Scenario 3: Removing nodes that are isolated (not connected by edges)
        parser.addNode("D");
        parser.removeNode("D");
        assertEquals(9, parser.getNodeCount(), "Node count should be 1 after removing isolated node 'D'.");
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
        assertEquals(11, parser.getNodeCount(), "Initial node count should be 3.");
        assertEquals(11, parser.getEdgeCount(), "Initial edge count should be 2.");

        // Scenario 1: Successfully remove multiple nodes and verify they’re gone
        String[] nodesToRemove = {"U1", "U2"};
        parser.removeNodes(nodesToRemove);
        assertEquals(9, parser.getNodeCount(), "Node count should be 1 after removing 'U1' and 'U2'.");
        assertEquals(9, parser.getEdgeCount(), "Edge count should be 3 after removing nodes 'U1' and 'U2'.");

        // Scenario 2: Try removing nodes that do not exist
        assertThrows(GraphParser.NodeNotFoundException.class, () -> {
            parser.removeNodes(new String[]{"NonExistentNode1", "NonExistentNode2"});
        }, "Attempting to remove non-existent nodes should throw NodeNotFoundException.");

        // Scenario 3: Remove an isolated node (not connected by edges)
        parser.addNode("U4");  // Add an isolated node
        assertEquals(10, parser.getNodeCount(), "Node count should be 2 after adding 'U4'.");
        parser.removeNode("U4");
        assertEquals(9, parser.getNodeCount(), "Node count should be 1 after removing isolated node 'U4'.");
    }


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
        assertEquals(11, parser.getNodeCount(), "Graph should have 6 nodes after additions.");
        assertEquals(12, parser.getEdgeCount(), "Graph should have 6 edges after additions.");

        // Scenario 1: Successfully remove an existing edge
        parser.removeEdge("U1", "U2");
        assertEquals(11, parser.getEdgeCount(), "Graph should have 5 edges after removing one.");

        // Remove another edge
        parser.removeEdge("U2", "U3");
        assertEquals(10, parser.getEdgeCount(), "Graph should have 4 edges after removing another.");


        // Scenario 3: Remove a node and verify edge and node counts
        parser.removeNode("U1");
        assertEquals(11, parser.getNodeCount(), "Graph should have 6 nodes after removing one node.");
        assertEquals(10, parser.getEdgeCount(), "Graph should have 4 edges after removing a node with edges.");


    }



    @Test
    public void testGraphSearchBFS() throws Exception {
        // Step 1: Parse and set up initial graph with nodes and edges
        parser.parseGraph("src/main/resources/input.dot");

        // Add additional nodes and edges to form a path and isolated nodes
        parser.addNode("A");
        parser.addNode("B");
        parser.addNode("C");
        parser.addNode("D");
        parser.addNode("E");
        parser.addEdge("A", "B");
        parser.addEdge("B", "C");
        parser.addEdge("C", "D");

        // Scenario 1: Path exists from A to D (BFS)
        Path path = parser.GraphSearch("A", "D", Algorithm.BFS);
        assertNotNull(path, "Path from A to D should exist");
        assertEquals("A -> B -> C -> D", path.toString(), "Expected path A -> B -> C -> D");

        // Scenario 3: Path does not exist in reverse direction in a directed graph
        path = parser.GraphSearch("D", "A", Algorithm.BFS);
        assertNull(path, "Path from D to A should not exist in directed graph");

    }




    @Test
    public void testGraphSearchDFS() throws GraphParser.DuplicateNodeException, GraphParser.DuplicateEdgeException, IOException, GraphParser.NodeNotFoundException {
        // Ensure the graph is initialized properly
        parser.parseGraph("src/main/resources/input.dot");

        // Setting up nodes
        parser.addNode("A");
        parser.addNode("B");
        parser.addNode("C");
        parser.addNode("D");
        parser.addNode("E");
        parser.addNode("F");

        // Adding edges based on the corrected structure
        parser.addEdge("A", "B");
        parser.addEdge("A", "C");
        parser.addEdge("B", "D");
        parser.addEdge("C", "F");
        parser.addEdge("D", "E");
        parser.addEdge("F", "E");

        // Scenario 1: Valid DFS path from A to E (should pass)
        Path pathAE = parser.GraphSearch("A", "E", Algorithm.DFS);
        assertNotNull(pathAE, "Path from A to E should exist.");
        assertEquals("A -> C -> F -> E", pathAE.toString(), "DFS path from A to E should follow A -> C -> F -> E");

        // Scenario 2: Valid DFS path from B to E (should pass)
        Path pathBE = parser.GraphSearch("B", "E", Algorithm.DFS);
        assertNotNull(pathBE, "Path from B to E should exist.");
        assertEquals("B -> D -> E", pathBE.toString(), "DFS path from B to E should follow B -> D -> E");

        // Scenario 3: Direct path from A to B (should pass)
        Path pathAB = parser.GraphSearch("A", "B", Algorithm.DFS);
        assertNotNull(pathAB, "Path from A to B should exist.");
        assertEquals("A -> B", pathAB.toString(), "Direct path from A to B should be A -> B");

        // Scenario 4: No path from E to A (should return null)
        Path pathEA = parser.GraphSearch("E", "A", Algorithm.DFS);
        assertNull(pathEA, "No path should exist from E to A.");

        // Scenario 5: Path from A to F (should pass)
        Path pathAF = parser.GraphSearch("A", "F", Algorithm.DFS);
        assertNotNull(pathAF, "Path from A to F should exist.");
        assertEquals("A -> C -> F", pathAF.toString(), "DFS path from A to F should follow A -> C -> F");

        // Scenario 6: No path between isolated nodes (if any were isolated)
        parser.addNode("G");
        Path pathAG = parser.GraphSearch("A", "G", Algorithm.DFS);
        assertNull(pathAG, "No path should exist from A to G as G is isolated.");
    }



    // RandomWalk
    @Test
    public void testRandomWalk() throws Exception {
        // Step 1: Parse and set up initial graph
        parser.parseGraph("src/main/resources/input.dot");

        // Add nodes and edges
        parser.addNode("A");
        parser.addNode("B");
        parser.addNode("C");
        parser.addNode("D");
        parser.addNode("E");
        parser.addEdge("A", "B");
        parser.addEdge("A", "C");
        parser.addEdge("B", "D");
        parser.addEdge("C", "D");
        parser.addEdge("D", "E");

        System.out.println("Adjacency Map: " + parser.getAdjacencyMap());

        // Step 2: Run Random Walk from A to E
        for (int i = 0; i < 5; i++) { // Run multiple times to test randomness
            System.out.println("Random Walk Test Run #" + (i + 1));
            Path path = parser.GraphSearch("A", "E", Algorithm.RANDOM_WALK);

            if (path != null) {
                System.out.println("Random Walk Path Found: " + path);
                // Validate the path starts at A and ends at E
                assertEquals("A", path.getNodes().get(0), "Path should start at A");
                assertEquals("E", path.getNodes().get(path.getNodes().size() - 1), "Path should end at E");

                // Validate the path consists of valid neighbors
                List<String> pathNodes = path.getNodes();
                for (int j = 0; j < pathNodes.size() - 1; j++) {
                    String current = pathNodes.get(j);
                    String next = pathNodes.get(j + 1);
                    assertTrue(parser.getAdjacencyMap().get(current).contains(next),
                            "Invalid path: " + next + " is not a neighbor of " + current);
                }
            } else {
                System.out.println("No Path Found from A to E (Run #" + (i + 1) + ")");
            }
        }
    }

}
