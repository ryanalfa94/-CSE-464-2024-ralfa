import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

import java.io.File;
import java.io.IOException;

    public class GraphParser {
        private MutableGraph graph;

        // Feature 1: Parses a DOT file and creates a graph
        public void parseGraph(String filepath) throws IOException {
            this.graph = new Parser().read(new File(filepath));
            System.out.println("Graph parsed successfully.");
        }

        // Output the graph details like number of nodes and edges
        public void printGraphDetails() {
            if (graph != null) {
                System.out.println("Number of nodes: " + graph.nodes().size());
                graph.nodes().forEach(node -> System.out.println("Node label: " + node.name()));
                System.out.println("Number of edges: " + graph.edges().size());
                graph.edges().forEach(edge -> System.out.println("Edge from " + edge.from().name() + " to " + edge.to().name()));
            } else {
                System.out.println("Graph is not initialized.");
            }
        }

        // Save the graph to a DOT file
        public void outputGraph(String filepath) throws IOException {
            if (graph != null) {
                Graphviz.fromGraph(graph).render(Format.DOT).toFile(new File(filepath));
                System.out.println("Graph saved to " + filepath);
            } else {
                System.out.println("Graph is not initialized.");
            }
        }

        // Save the graph to a PNG file
        public void outputGraphAsPng(String filepath) throws IOException {
            if (graph != null) {
                Graphviz.fromGraph(graph).width(500).render(Format.PNG).toFile(new File(filepath));
                System.out.println("Graph image saved to " + filepath);
            } else {
                System.out.println("Graph is not initialized.");
            }
        }

        public static void main(String[] args) {
            GraphParser parser = new GraphParser();
            try {
                // Replace with your actual DOT file path
                parser.parseGraph("src/main/resources/input.dot");
                parser.printGraphDetails();
                // Replace with your actual output path
                parser.outputGraph("src/main/resources/output.dot");
                parser.outputGraphAsPng("src/main/resources/output.png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


