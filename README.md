Graph Parser Project
This project implements a graph parser that reads DOT files, allows manipulation of the graph by adding nodes and edges, and outputs the graph in both DOT and PNG formats using the Graphviz Java library.

Features
Parse DOT Files:

Parses a graph described in DOT format and initializes a directed graph.
Example DOT format:
dot
Copy code
digraph G {
    A -> B;
    B -> C;
    C -> A;
}
Add Nodes:

Allows the user to add new nodes to the graph and ensures no duplicate nodes are added.
Example code:

parser.addNode("G");
Add Edges:

Adds new edges between nodes and checks for duplicate edges.
Example code:

parser.addEdge("A", "B");
Output Graph:

Outputs the graph in DOT format or as a PNG image.
Requirements
Java 11 or later
Maven (for building and running tests)

Setup

Clone the repository:
git clone <your-repo-url>
cd <your-repo-directory>

Build the project with Maven:


mvn clean package
Running the Program
You can run the program directly in your IDE or from the command line. Ensure the input DOT file is placed in the src/main/resources/ directory.

Example Usage
Parsing a Graph:

parser.parseGraph("src/main/resources/input.dot");
Adding Nodes and Edges:

parser.addNode("G");
parser.addEdge("A", "B");
Output Graph:

parser.outputGraph("src/main/resources/output.dot");
parser.outputGraphAsPng("src/main/resources/output.png");
Running Tests
To run the tests, use Maven:

mvn test
Example Test Case
The following is an example test for adding a node:

@Test
public void testAddNode() throws GraphParser.DuplicateNodeException {
    parser.parseGraph("src/main/resources/input.dot");
    parser.addNode("G");
    assertEquals(4, parser.getNodeCount());
}
Example Input and Output
Input (input.dot):

digraph G {
    A -> B;
    B -> C;
    C -> A;
}
Expected Output:

Number of nodes: 4
Number of edges: 3
Nodes: A, B, C, G
Edges: A -> B, B -> C, C -> A


Project Part 2 starts Here: 
Testing CI
