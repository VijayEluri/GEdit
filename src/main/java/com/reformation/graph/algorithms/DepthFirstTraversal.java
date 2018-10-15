package com.reformation.graph.algorithms;

import com.reformation.graph.Edge;
import com.reformation.graph.Graph;
import com.reformation.graph.Node;
import com.reformation.graph.Path;
import com.reformation.graph.PathContainer;
import com.reformation.graph.PathException;

import java.util.ArrayList;


/**
 * Performs a Depth First Traversal on a Graph.
 * Created on Jun 7, 2004
 * @author Randy Secrist
 */
public class DepthFirstTraversal extends Algorithm {

    /**
     * Used to index the search function.
     */
    private int search_index;

    /**
     * Used to hold graph state.
     */
    private Node[] nodes;

    /**
     * Final path results.
     */
    private ArrayList<PathElement> results;

    /**
     * Constructs the default DepthFirstTraversal Sort Algorithm.
     */
    public DepthFirstTraversal() {
        super();
    }

    /**
     * Constructs a DepthFirstTraversal Sort Algorithm with a Visitor.
     * @param v The visitor to use during traversal.
     */
    public DepthFirstTraversal(Visitor v) {
        super();
        this.visitor = v;
    }

    /**
     * Returns the menu name for this algorithm.
     * @see com.reformation.graph.algorithms.Algorithm#getMenuName()
     */
    public String getMenuName() {
        return "Depth First Traversal";
    }

    /**
     * Entry point into this algorithm.
     * Returns the path generated by this algorithm.
     * @see com.reformation.graph,algorithms.Algorithm#startAlgorithm(com.reformation.graph.Graph)
     */
    protected PathContainer startAlgorithm(Graph g) {
        Node n = showSourceDialog(g);
        if (n != null) {
            PathContainer pc = doAlgorithm(g, n.getId());
            if (pc.size() == 0)
                this.showWarningDialog("Depth First Traversal could not be performed.");
            return pc;
        }
        else
            return new PathContainer();
    }

    /**
     * Performs the work of this algorithm.
     */
    public PathContainer doAlgorithm(Graph g, int begin) {
        PathContainer rtnVal = new PathContainer();
        search(g, begin, false);

        Node[] nodes0 = new Node[results.size()];
        Edge[] edges0 = new Edge[results.size()];
        for (int i = 0; i < results.size(); i++) {
            PathElement elm = (PathElement) results.get(i);
            nodes0[i] = elm.getNode();
            edges0[i] = elm.getEdge();
        }

        // Set PathContainer
        try {
            rtnVal.addPath(new Path(nodes0, edges0, 1));
        }
        catch (PathException e) {}

        return rtnVal;
    }

    /**
     * Searches the graph for any connected nodes.
     * @param g The Graph to use during the search.
     * @param begin The node in the graph to start with.
     * @param all Determines if all nodes are visited, or just ones connected to begin.
     */
    private void search(Graph g, int begin, boolean all) {
        // Obtain reference to nodes.
        nodes = g.getNodes();

        // reset graph visited status
        for (int i = 0; i < nodes.length; i++) {
            Node n = nodes[i];
            n.setVisited(Node.FALSE);
        }
        // reset class internals
        search_index = 0;
        results = new ArrayList<PathElement>();

        if (!(nodes.length > 0))
            return;

        // set begin point
        Node temp = nodes[0];
        for (int i = 0; i < nodes.length; i++) {
            if (begin == nodes[i].getId()) {
                nodes[0] = nodes[i];
                nodes[i] = temp;
                break;
            }
        }

        // Do we visit all nodes, or just connected ones?
        int end;
        if (all)
            end = nodes.length;
        else
            end = 1;

        // Visit nodes
        for (int i = 0; i < end; i++) {
            Node n = nodes[i];
            if (n.getVisited() == Node.FALSE) {
                results.add(new PathElement(n, null));
                visit(g,n);
            }
            else
                results.add(new PathElement(null, null));
        }
    }

    /**
     * Recursivly marks out and visits nodes.
     * @param g The graph to use.
     * @param x The Node to visit.
     */
    private void visit(Graph g, Node x) {
        x.setVisited(++search_index); visitor.visit(x);
        for (int i = 0; i < nodes.length; i++) {
            Node y = nodes[i];
            if (g.isEdge(x.getId(), y.getId())) {
                if (y.getVisited() == Node.FALSE) {
                    Edge e = g.getEdge(x.getId(), y.getId());
                    results.add(new PathElement(y, e)); visitor.visit(e);
                    visit(g,y);
                }
                else
                    results.add(new PathElement(null, null));
            }
        }
    }

    /**
     * Returns true if this algorithm can be
     * executed on the input graph, false otherwise.
     * @see com.reformation.graph.algorithms.Algorithm#works(com.reformation.graph.Graph)
     */
    public boolean works(Graph g) {
        if (g.isDirected())
            return false;
        else if (g.size() < 1)
            return false;
        return true;
    }
}

/**
 * Represents a single step in a path for this algorithm.
 * @author Randy Secrist
 */
class PathElement {
    private Node n;
    private Edge e;

    PathElement(Node n, Edge e) {
        this.n = n;
        this.e = e;
    }

    /**
     * @return Returns the e.
     */
    public Edge getEdge() {
        return e;
    }
    /**
     * @param e The e to set.
     */
    public void setEdge(Edge e) {
        this.e = e;
    }
    /**
     * @return Returns the n.
     */
    public Node getNode() {
        return n;
    }
    /**
     * @param n The n to set.
     */
    public void setNode(Node n) {
        this.n = n;
    }
}
