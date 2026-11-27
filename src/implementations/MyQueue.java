/*
 * FIFO queue of Strings backed by MyDLL. Index 0 is the front: enqueue adds
 * to the back, dequeue and peek use the front.
 */
package implementations;

import exceptions.EmptyQueueException;
import utilities.Iterator;
import utilities.QueueADT;

/**
 * A doubly linked list-backed implementation of the {@link QueueADT} interface,
 * for use in CPRG 304 at SAIT.
 * <p>
 * This class delegates all storage to an internal {@link MyDLL}, treating
 * index {@code 0} as the front of the queue and the last index as the back.
 * {@link #enqueue(String)} appends to the back; {@link #dequeue()} and
 * {@link #peek()} access the front. A queue follows a
 * <strong>First-In, First-Out (FIFO)</strong> ordering policy: the first
 * element enqueued is the first one returned.
 * </p>
 *
 * <p><strong>Design Note – String-Specific Implementation:</strong><br>
 * This class is intentionally designed to store {@link String} elements only.
 * A fully reusable, type-safe design would use Java generics (e.g.,
 * {@code MyQueue<E>}), allowing any object type to be stored in the queue.
 * Generic programming is outside the scope of CPRG 304 and has therefore
 * been omitted. All element fields, parameters, and return types are fixed
 * as {@link String} throughout this class and its implementations.
 * </p>
 */
public class MyQueue implements QueueADT
{
    // fields

    /** the linked list holding the queue, index 0 is the front */
    private MyDLL list;

    // constructors

    /**
     * makes a new empty queue.
     */
    public MyQueue()
    {
        list = new MyDLL();
    }

    // queue operations

    /**
     * adds an element to the back of the queue.
     *
     * @param toAdd  the string to add, can't be null
     * @throws NullPointerException if toAdd is null
     */
    @Override
    public void enqueue( String toAdd ) throws NullPointerException
    {
        if ( toAdd == null )
        {
            throw new NullPointerException( "Can't enqueue a null element." );
        }

        // the end of the list is the back of the queue
        list.add( toAdd );
    }

    /**
     * removes and returns the element at the front of the queue.
     *
     * @return the element that was at the front
     * @throws EmptyQueueException if the queue is empty
     */
    @Override
    public String dequeue() throws EmptyQueueException
    {
        if ( list.isEmpty() )
        {
            throw new EmptyQueueException( "Can't dequeue from an empty queue." );
        }

        // index 0 is the front of the queue
        return list.remove( 0 );
    }

    /**
     * returns the element at the front of the queue without removing it.
     *
     * @return the element at the front
     * @throws EmptyQueueException if the queue is empty
     */
    @Override
    public String peek() throws EmptyQueueException
    {
        if ( list.isEmpty() )
        {
            throw new EmptyQueueException( "Can't peek at an empty queue." );
        }
        return list.get( 0 );
    }

    /**
     * removes everything from the queue.
     */
    @Override
    public void dequeueAll()
    {
        list.clear();
    }

    // query methods

    /**
     * checks if the queue has nothing in it.
     *
     * @return true if the queue is empty, false if not
     */
    @Override
    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    /**
     * returns how many elements are in the queue.
     *
     * @return the number of elements in the queue
     */
    @Override
    public int size()
    {
        return list.size();
    }

    /**
     * checks if the element is somewhere in the queue.
     *
     * @param toFind  the string to look for, can't be null
     * @return true if it's in the queue, false if not
     * @throws NullPointerException if toFind is null
     */
    @Override
    public boolean contains( String toFind ) throws NullPointerException
    {
        if ( toFind == null )
        {
            throw new NullPointerException( "Can't search for a null element." );
        }
        return list.contains( toFind );
    }

    /**
     * finds how far from the front the element is, counting the front as 1.
     *
     * @param toFind  the string to look for
     * @return the 1-based position from the front, or -1 if it's not in the queue
     */
    @Override
    public int search( String toFind )
    {
        // a null can never be in the queue, so it's just not found
        if ( toFind == null )
        {
            return -1;
        }

        int index = list.indexOf( toFind );

        // list indexes start at 0, queue positions start at 1
        return ( index == -1 ) ? -1 : index + 1;
    }

    /**
     * checks if another queue has the same elements in the same order.
     *
     * @param that  the queue to compare with
     * @return true if both queues hold equal elements in the same order, false if not
     */
    @Override
    public boolean equals( QueueADT that )
    {
        if ( that == null || this.size() != that.size() )
        {
            return false;
        }

        // walk both queues front to back and compare each pair
        Iterator mine = this.iterator();
        Iterator theirs = that.iterator();
        while ( mine.hasNext() )
        {
            if ( !mine.next().equals( theirs.next() ) )
            {
                return false;
            }
        }
        return true;
    }

    // conversion methods

    /**
     * gives back an iterator that goes from the front of the queue to the back.
     *
     * @return a new iterator for the queue
     */
    @Override
    public Iterator iterator()
    {
        return list.iterator();
    }

    /**
     * copies the queue into an array, front first. if the given array is too
     * small, a new one that fits is made instead.
     *
     * @param holder  the array to copy into, can't be null
     * @return an array with everything in the queue, front to back
     * @throws NullPointerException if holder is null
     */
    @Override
    public String[] toArray( String[] holder ) throws NullPointerException
    {
        if ( holder == null )
        {
            throw new NullPointerException( "The holder array can't be null." );
        }
        return list.toArray( holder );
    }
}
