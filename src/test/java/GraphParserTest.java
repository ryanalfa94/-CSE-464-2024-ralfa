import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GraphParserTest {
    private GraphParser parser;

    @BeforeEach
    public void setup() {
        parser = new GraphParser();
    }

    @Test
    public void testParseGraph() throws IOException {
        // Input file path for testing
        String inputFilePath = "src/main/resources/input.dot";
        parser.parseGraph(inputFilePath);

        String expected = Files.readString(Paths.get("src/main/resources/expected_output.txt")).replace("\r\n", "\n");
        String actualOutput = parser.getGraphDetails().replace("\r\n", "\n");

        assertEquals(expected, actualOutput);
    }

    @Test
    public void testAddNode() throws IOException {
        parser.parseGraph("src/main/resources/input.dot");

        try {
            // Add node "A" and "B"
            parser.addNode("A");
            parser.addNode("B");

            // Add assertions to check node addition logic
            assertEquals(2, parser.getNodeCount());

            // Testing duplicate node exception
            assertThrows(GraphParser.DuplicateNodeException.class, () -> parser.addNode("A"), "Duplicate node A should throw exception.");
        } catch (GraphParser.DuplicateNodeException e) {
            e.printStackTrace();
        }
    }


}