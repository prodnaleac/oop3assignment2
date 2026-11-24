package implementations;

/**
 * A single node in a doubly linked list (DLL), used as the building block
 * for {@link MyDLL} in CPRG 304 at SAIT.
 * <p>
 * Each node stores a {@link String} element and maintains two directional
 * references: a predecessor ({@code pred}) pointing to the node immediately
 * before it in the chain, and a successor ({@code succ}) pointing to the
 * node immediately after it. A {@code null} predecessor indicates the node
 * is the head of the list; a {@code null} successor indicates it is the tail.
 * </p>
 *
 * <p><strong>Design Note – String-Specific Implementation:</strong><br>
 * This class is intentionally designed to store {@link String} elements only.
 * A fully reusable, type-safe design would use Java generics (e.g.,
 * {@code DLLNode<E>}), allowing any object type to be stored in the node.
 * Generic programming is outside the scope of CPRG 304 and has therefore
 * been omitted. All element fields, parameters, and return types are fixed
 * as {@link String} throughout this class and its usages.
 * </p>
 */
public class MyDLLNode
{
	private String element;
	private MyDLLNode pred;
	private MyDLLNode succ;
	
    /**
     * Constructs a node holding the given element, with no neighbours yet.
     *
     * @param element the String to store in this node
     */
	public MyDLLNode( String element ) {
		this.element = element;
		this.pred = null;
		this.succ = null;
	}
	
	 /**
     * Returns the element stored in this node.
     *
     * @return the stored String
     */
	public String getElement() { 
		return element; 
	}
	
	/**
     * Replaces the element stored in this node.
     *
     * @param element the new String to store
     */
	public void setElement( String element ) { 
		this.element = element; 
	}
	
	 /**
     * Returns the node before this one.
     *
     * @return the predecessor node, or null if this node is the head
     */
	public MyDLLNode getPred() {
		return pred;	
	}
	
	 /**
     * Sets the node before this one.
     *
     * @param pred the node to link as the predecessor, or null
     */
	public void setPred( MyDLLNode pred ) {
		this.pred = pred;
	}
	
	/**
     * Returns the node after this one.
     *
     * @return the successor node, or null if this node is the tail
     */
	public MyDLLNode getSucc() {
		return succ;
	}
	
	/**
     * Sets the node after this one.
     *
     * @param succ the node to link as the successor, or null
     */
	public void setSucc ( MyDLLNode succ ) {
		this.succ = succ;
	}
}