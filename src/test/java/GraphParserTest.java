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
}
