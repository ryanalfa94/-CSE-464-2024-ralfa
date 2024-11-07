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
        assertEquals(5, parser.getNodeCount(), "Node count should be 5 after removing 'P'.");
        assertEquals(4, parser.getEdgeCount(), "Edge count should be 4 after removing node 'P' and its connected edges.");

        // Scenario 2: Try removing a node that does not exist
        assertThrows(GraphParser.NodeNotFoundException.class, () -> {
            parser.removeNode("Q");
        }, "Attempting to remove a non-existent node 'C' should throw NodeNotFoundException.");

        // Scenario 3: Removing nodes that are isolated (not connected by edges)
        parser.addNode("D");
        parser.removeNode("D");
        assertEquals(6, parser.getNodeCount(), "Node count should be 1 after removing isolated node 'D'.");
    }






}
