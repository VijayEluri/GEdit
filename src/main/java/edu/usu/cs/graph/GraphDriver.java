package edu.usu.cs.graph;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.usu.cs.algorithms.Algorithm;
import edu.usu.cs.algorithms.SpanningTree;
import edu.usu.cs.algorithms.Topological;
import edu.usu.cs.algorithms.Visitor;
import edu.usu.cs.gui.GEdit;
import edu.usu.cs.types.Action;
import edu.usu.cs.types.ActionConfig;
import edu.usu.cs.types.Transition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Command line driver for a graph class. This was used as a quick and dirty
 * testing tool to verify both the graph class, and the graphical interface.
 * Creation date: (2/2/2002 1:26:23 PM)
 *
 * @author Randy Secrist
 */
public class GraphDriver {

    private Logger log = LoggerFactory.getLogger(GraphDriver.class);

    private static final String DEFAULT_DATA_WRAPPER = "edu.usu.cs.graph.StringObj";
    private Data dataSpawn = null;
    private static String namedWrapper;

    public static void main(String[] argv) {
        namedWrapper = GEdit.processArgs(argv, "edu.usu.cs.graph.StringObj");
        GraphDriver gd = new GraphDriver();
    }

    /**
     * Constructs a new graph driver.
     */
    public GraphDriver() {
        super();
        Graph graph = new Graph();
        graph.setDirected(true);
        System.out.println("Directed Status - " + graph.isDirected());
        try {
            int a = graph.addNode(getData("a"));
            int b = graph.addNode(getData("b"));
            int c = graph.addNode(getData("c"));
            int d = graph.addNode(getData("d"));
            int e = graph.addNode(getData("e")); // island node
            int f = graph.addNode(getData("f"));
            int g = graph.addNode(getData("g"));

            // Test node integrity.
            System.out.println("Node List");
            Node[] nodes = graph.getNodes();
            for (int i = 0; i < nodes.length; i++) {
                if (nodes[i] != null) {
                    Data obj = nodes[i].getData();
                    if (obj != null)
                        System.out.println(nodes[i].getData().getDisplayName());
                    else
                        System.out.println("DATA NULL AT " + i);
                }
                else
                    System.out.println("NODE NULL AT " + i);
            }
            graph.addEdge(new Edge(a, f, 9.0));
            graph.addEdge(new Edge(f, b, 2.0));
            graph.addEdge(new Edge(a, b, 1.0));
            graph.addEdge(new Edge(c, d, 7.0));
            graph.addEdge(new Edge(g, d, 5));
            // Test edge integrity
            System.out.println("Edge List");
            Edge[] edges = graph.getEdges();
            for (int i = 0; i < edges.length; i++) {
                if (edges[i] != null) {
                    System.out.println(edges[i].getSource() + " - " + edges[i].getDest() + " - " + edges[i].getWeight());
                }
                else
                    System.out.println("EDGE NULL AT " + i);
            }
            System.out.println("Island Count: " + graph.getIslandCount());
            for (int i = 0; i < graph.getIslandCount(); i++) {
                Node[] nod = graph.getNodes(i);
                Edge[] edg = graph.getEdges(i);
                for (int j = 0; j < nod.length; j++) {
                    System.out.println("Island #" + i + "::" + nod[j].getData().getDisplayName() + "::" + nod[j].getId());
                }
                for (int k = 0; k < edg.length; k++) {
                    System.out.println("Island #" + i + "::" + edg[k].getSource() + "::" + edg[k].getDest() + "::" + edg[k].getWeight());
                }
            }

            // Remove Node (can remove a thru f) g.removeNode(f);
            // Remove edges
            graph.removeEdge(graph.getEdge(a,b));

            // Print Status:
            System.out.println("\n\n----- NODES REMOVED -----\n");
            System.out.println("Island Count: " + graph.getIslandCount());
            for (int i = 0; i < graph.getIslandCount(); i++) {
                Node[] nod = graph.getNodes(i); Edge[] edg = graph.getEdges(i);
                for (int j = 0; j < nod.length; j++) { System.out.println( "Island #" + i + "::" + nod[j].getData().getDisplayName() + "::" + nod[j].getId()); }
                for (int k = 0; k < edg.length; k++) { System.out.println( "Island #" + i + "::" + edg[k].getSource() + "::" + edg[k].getDest() + "::" + edg[k].getWeight()); }
            }
            this.runAlgorithm(graph);
            System.out.println("Directed Status after run - " + graph.isDirected());
            System.out.println("GraphDriver::GraphDriver - All Done! - Exiting..."); System.exit(0);

            this.toJson(graph);
        }
        catch (java.lang.Throwable e) {
            e.printStackTrace();
        }
    }

    private Data getData(String val) throws Exception {
        if (dataSpawn == null) {
            // create data
            try {
                dataSpawn = ((Data) Class.forName(namedWrapper).newInstance()).getInstance(val);
            }
            catch (Exception e) {
                dataSpawn = null;
                System.out.println("getNewData::GraphDriver - Unable to instantiate named data wrapper! (" + namedWrapper + ")");
            }
            // use default wrapper?
            if (dataSpawn == null) {
                try {
                    dataSpawn = GEdit.parseNodeString(val);
                }
                catch (Exception e) {
                    log.warn("Could not create instance of default data wrapper.", e);
                    System.exit(1);
                }
            }
            return dataSpawn;
        }
        else
            return dataSpawn.getInstance(val);
    }

    public void runAlgorithm(Graph g) {
        // Replace the following with your algorithm.
        SpanningTree algorithm = new SpanningTree();
        if (algorithm.works(g)) {
            PathContainer pc = algorithm.runMe(g);
            StringBuffer buf = new StringBuffer();
            Path[] paths = pc.getPaths();
            for (int i = 0; i < paths.length; i++) {
                // # Nodes will == # of Edges (but edges could be null.
                // For ith element, print Node first, then Edge.
                Node[] nodes = paths[i].getNodePath();
                Edge[] edges = paths[i].getEdgePath();
                for (int j = 0; j < nodes.length; j++) {
                    // Print Node
                    if (nodes != null && j < nodes.length && nodes[j] != null)
                        buf.append("Path #")
                           .append(i + 1)
                           .append("\tNode #")
                           .append(j + 1)
                           .append("\t")
                           .append(nodes[j].getData().getDisplayName())
                           .append("\n");
                    // Print Edge
                    if (edges != null && j < edges.length && edges[j] != null)
                        buf.append("Path #")
                           .append(i + 1)
                           .append("\tEdge #")
                           .append(j + 1)
                           .append(" - (Src) - (Dest)\t")
                           .append(edges[j].getSource())
                           .append(" - ")
                           .append(edges[j].getDest())
                           .append("\n");
                }
            }
            System.out.println("--- ALGORITHM RESULTS ---\n");
            System.out.println(buf.toString());
        }
        else {
            System.out.println("--- ALGORITHM NOT COMPATIBLE WITH GRAPH ---");
        }
    }

    public static void toJson(Graph graph) {
        ObjectMapper mapper = new ObjectMapper();
        final ObjectNode graphJson = mapper.createObjectNode();
        graphJson.put("id", graph.getId());

        ArrayNode actions = mapper.createArrayNode();
        Algorithm loader = new Topological(new Visitor() {
            public void visit(Edge e) { ; }
            public void visit(Node n) {
                Edge[] children = graph.getEdgesByNodeId(n.getId());
                if (n.getData() instanceof Action) {
                    Action a = (Action) n.getData();
                    ObjectNode actionJson = mapper.createObjectNode();
                    actionJson.put("id", n.getId() + 1);
                    actionJson.put("type", a.getType());
                    actionJson.put("version", a.getVersion());

                    ArrayNode actionConfigs = mapper.createArrayNode();
                    List<ActionConfig> configs = a.getConfig();

                    configs.stream().forEach(config -> {
                        try {
                            actionConfigs.addPOJO(mapper.writeValueAsString(config));
                        }
                        catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    });

                    // map source node to transition source
                    ArrayNode transitionConfigs = mapper.createArrayNode();
                    Arrays.stream(children).forEach(child -> {
                        ObjectNode transitionJson = mapper.createObjectNode();
                        if (child.getData().isPresent()) {
                            Transition transition = (Transition) child.getData().get();
                            try {
                                transitionJson.put("condition", mapper.writeValueAsString(transition.getCondition()));
                            }
                            catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }
                        transitionJson.put("target", child.getDest() + 1);
                        transitionConfigs.addPOJO(transitionJson);
                    });
                    actionJson.putPOJO("config", actionConfigs);
                    actionJson.putPOJO("transitions", transitionConfigs);

                    actions.add(actionJson);
                }
            }
        }, true);
        loader.runMe(graph);
        graphJson.putPOJO("actions", actions);

        System.out.println(graphJson.toString());
    }
}