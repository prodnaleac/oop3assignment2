package implementations;

import java.util.Arrays;
import java.util.NoSuchElementException;
import utilities.Iterator;
import utilities.ListADT;

/**
 * A doubly linked list (DLL) implementation of the {@link ListADT} interface,
 * for use in CPRG 304 at SAIT.
 * <p>
 * This class stores {@link String} elements in a chain of {@link MyDLLNode}
 * objects, each of which holds a reference to both its predecessor ({@code pred})
 * and its successor ({@code succ}). The list maintains explicit {@code head}
 * and {@code tail} references for O(1) access to both ends of the chain.
 * Insertions and removals at arbitrary positions require O(n) traversal.
 * All index parameters are zero-based unless otherwise noted.
 * </p>
 *
 * <p><strong>Design Note – String-Specific Implementation:</strong><br>
 * This class is intentionally designed to store {@link String} elements only.
 * A fully reusable, type-safe design would use Java generics (e.g.,
 * {@code MyDLL<E>}), allowing any object type to be stored in the list.
 * Generic programming is outside the scope of CPRG 304 and has therefore
 * been omitted. All element fields, parameters, and return types are fixed
 * as {@link String} throughout this class and its implementations.
 * </p>
 */
public class MyDLL implements ListADT
{
	/** The first node in the list, or null if the list is empty. */
    private MyDLLNode head;
    
    /** The last node in the list, or null if the list is empty. */
    private MyDLLNode tail;
    
    /** The number of elements currently stored in the list. */
    private int size;
    
    /**
     * Constructs a new, empty doubly linked list.
     */
    public MyDLL() {
    	head = null;
    	tail = null;
    	size = 0;
    }
    
    /**
     * Returns the number of elements currently in the list.
     *
     * @return the current size of the list; never negative
     */
    @Override
    public int size() {
    	return size;
    }
    
    /**
     * Returns true if the list contains no elements.
     *
     * @return true if the size is 0; false otherwise
     */
    @Override
    public boolean isEmpty() {
    	return size == 0;
    }
    
    /**
     * Removes all elements from the list, leaving it empty.
     */
    @Override
    public void clear() {
    	head = null;
    	tail = null;
    	size = 0;
    }
    
    /**
     * Finds the node at the given index, starting the walk from whichever
     * end of the list is closer. The caller must ensure the index is valid.
     *
     * @param index the zero-based index of the node to find; must be in [0, size - 1]
     * @return the node at that index
     */
    private MyDLLNode getNode( int index ) {
    	MyDLLNode current;
    	if ( index < size / 2 ) {
    		current = head;
    		for( int i = 0; i < index; i++ ) {
    			current = current.getSucc();
    		}
    	} else {
    		current = tail;
    		for ( int i = size - 1; i > index; i-- ) {
    			current = current.getPred();
    		}
    	}
    	return current;
    }
    
    /**
     * Removes the given node from the chain, updating head and tail as
     * needed, and decrements the size.
     *
     * @param node the node to remove; must currently be in this list
     * @return the element that was stored in the removed node
     */
    private String unlink( MyDLLNode node ) {
    	MyDLLNode before = node.getPred();
    	MyDLLNode after = node.getSucc();
    	
    	if ( before == null ) {
    		head = after;
    	} else {
    		before.setSucc( after );
    	}
    	if ( after == null ) {
    		tail = before;
    	} else {
    		after.setPred( before );
    	}
    	
    	node.setPred( null );
    	node.setSucc( null );
    	size--;
    	return node.getElement();
    }
    
    /**
     * Inserts the specified element at the given index, shifting existing
     * elements at or beyond that position one place to the right.
     *
     * @param index the zero-based position for the new element; valid range is [0, size()]
     * @param toAdd the String to insert; must not be null
     * @return true if the insertion was successful
     * @throws NullPointerException      if toAdd is null
     * @throws IndexOutOfBoundsException if index is less than 0 or greater than size()
     */
    @Override
    public boolean add( int index, String toAdd ) throws NullPointerException, IndexOutOfBoundsException {
    	if ( toAdd == null ) {
    		throw new NullPointerException( "Can't add a null element." );
    	}
    	if ( index < 0 || index > size ) {
    		throw new IndexOutOfBoundsException( "Index: " + index + ", Size: " + size );
    	}
    	
    	MyDLLNode newNode = new MyDLLNode( toAdd );
    	
    	if ( size == 0 ) { //list is empty
    		head = newNode;
    		tail = newNode;
    	} else if ( index == 0) { //new head
    		newNode.setSucc( head );
    		head.setPred( newNode );
    		head = newNode;
    	} else if ( index == size) { //new tail
    		newNode.setPred( tail );
    		tail.setSucc( newNode );
    		tail = newNode;
    	} else {  //in the middle
    		MyDLLNode after = getNode( index );
    		MyDLLNode before = after.getPred();
    		newNode.setPred( before );
    		newNode.setSucc( after );
    		before.setSucc( newNode );
    		after.setPred( newNode );
    	}
    	
    	size++;
    	return true;
    }
    
    /**
     * Appends the specified element to the end of the list.
     *
     * @param toAdd the String to append; must not be null
     * @return true if the element was successfully added
     * @throws NullPointerException if toAdd is null
     */
    @Override
    public boolean add( String toAdd ) throws NullPointerException {
    	return add( size, toAdd );
    }
    
    /**
     * Returns the element at the specified position in the list.
     *
     * @param index the zero-based index of the element; valid range is [0, size() - 1]
     * @return the element stored at the given index
     * @throws IndexOutOfBoundsException if index is less than 0 or greater than or equal to size()
     */
    @Override
    public String get( int index ) throws IndexOutOfBoundsException {
    	if ( index < 0 || index >= size ) {
    		throw new IndexOutOfBoundsException( "Index: " + index + ", Size: " + size );
    	}
    	return getNode( index ).getElement();
    }
    
    /**
     * Replaces the element at the specified position and returns the
     * element that was previously stored there.
     *
     * @param index    the zero-based index of the element to replace; valid range is [0, size() - 1]
     * @param toChange the new String to store; must not be null
     * @return the element previously stored at the specified index
     * @throws NullPointerException      if toChange is null
     * @throws IndexOutOfBoundsException if index is less than 0 or greater than or equal to size()
     */
    @Override
    public String set( int index, String toChange ) throws NullPointerException, IndexOutOfBoundsException {
    	if ( toChange == null ) {
    		throw new NullPointerException( "Cannot set a null element. ");
    	}
    	if ( index < 0 || index >= size ) {
    		throw new IndexOutOfBoundsException( "Index: " + index + ", Size: " + size );
    	}
    	
    	MyDLLNode node = getNode( index );
    	String old = node.getElement();
    	node.setElement( toChange );
    	return old;
    }
    
    /**
     * Removes and returns the element at the specified position, closing
     * the gap left behind.
     *
     * @param index the zero-based index of the element to remove; valid range is [0, size() - 1]
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException if index is less than 0 or greater than or equal to size()
     */
    @Override
    public String remove( int index ) throws IndexOutOfBoundsException {
    	if ( index < 0 || index >= size ) {
    		throw new IndexOutOfBoundsException( "Index: " + index + ", Size: " + size );
    	}
    	return unlink( getNode( index ) );
    }
}