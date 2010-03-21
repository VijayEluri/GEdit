package edu.usu.cs.graph;

/**
 * Node class will hold data of any type as long as it uses the Data interface
 * Creation date: (2/2/2002 10:59:46 AM)
 * 
 * @author Seth Humphries
 * @author Randy Secrist
 */
public class Node implements java.io.Serializable {

	/**
	 * Serial version uid.
	 */
	static final long serialVersionUID = -763388219244002231L;
	
	/**
	 * Indicates a FALSE value.
	 */
	public static final int FALSE = 0;
	
	/**
	 * Indicates a TRUE value.
	 */
	public static final int TRUE = 1;
	
	/**
	 * Boolean variable which may be used to indicate if an algorithm has
	 * passed by here.
	 */
	private int beenhere;
	
	/**
	 * The id of this node.
	 */
	private int id;
	
	/**
	 * The data this node holds.
	 */
	private Data obj;

	/**
	 * Default constructor which sets all internals to default values.
	 */
	Node() {
		this.id = -1;
		this.obj = new StringObj("");
	}

	/**
	 * Constructor which sets the data object and id of the node.
	 */
	public Node(Data obj, int id) {
		this.obj = obj;
		this.id = id;
	}

	/**
	 * use this as you back out of recursion to "cleanup" everything
	 */
	public synchronized void cleanUp() {
		beenhere = 0;
	}

	/**
	 * Returns the data this node holds. 
	 * @return The data object.
	 */
	public Data getData() {
		return obj;
	}

	/**
	 * Returns the ID of the node.
	 * @return The id of the node.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Determines if this node has been visited.
	 * @return 1 if the node has been visited, 0 otherwise.
	 */
	public int getVisited() {
		return beenhere;
	}

	/**
	 * Marks any internal data object as NULL, and sets it to the new value.
	 * @param obj The new data object.
	 */
	synchronized void setData(Data obj) {
		this.obj = null;
		this.obj = obj;
	}

	synchronized void setId(int tempid) {
		this.id = tempid;
	}

	/**
	 * Sets the visitor.
	 * @param beenhere The visitor to set.
	 */
	public synchronized void setVisited(int beenhere) {
		this.beenhere = beenhere;
	}

	/**
	 * Returns the display name of this node by using the
	 * contained Data object.
	 */
	public String toString() {
		return obj.getDisplayName();
	}
}