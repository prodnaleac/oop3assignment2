package implementations;

import java.util.Arrays;
import java.util.NoSuchElementException;
import utilities.Iterator;
import utilities.ListADT;

/**
 * A dynamic array-based implementation of the {@link ListADT} interface,
 * for use in CPRG 304 at SAIT.
 * <p>
 * This class stores {@link String} elements in an internally managed array
 * that grows automatically when capacity is reached. The growth amount is
 * determined by a configurable growth rate expressed as a decimal percentage
 * of the current array length (e.g., {@code 0.25} means 25% growth).
 * All index parameters are zero-based unless otherwise noted.
 * </p>
 *
 * <p><strong>Design Note – String-Specific Implementation:</strong><br>
 * This class is intentionally designed to store {@link String} elements only.
 * A fully reusable, type-safe design would use Java generics (e.g.,
 * {@code MyArrayList<E>}), allowing any object type to be stored in the list.
 * Generic programming is outside the scope of CPRG 304 and has therefore
 * been omitted. All element fields, parameters, and return types are fixed
 * as {@link String} throughout this class and its implementations.
 * </p>
 */
public class MyArrayList implements ListADT
{
    // constants

    /** starting size of the array */
    private static final int DEFAULT_CAPACITY = 10;

    /** how much the array grows when it's full (0.5 = grows by 50%) */
    private static final double DEFAULT_GROWTH_RATE = 0.5;

    // fields

    /** the array holding everything, only 0 to size - 1 is actually used */
    private String[] elements;

    /** how many elements are in the list right now */
    private int size;

    /** how much the array grows when it's full */
    private double growthRate;

    // constructors

    /**
     * makes an empty list with the default growth rate.
     */
    public MyArrayList()
    {
        this( DEFAULT_GROWTH_RATE );
    }

    /**
     * makes an empty list with a custom growth rate.
     *
     * @param growthRate  how much the array grows when it's full (like 0.25 for 25%)
     */
    public MyArrayList( double growthRate )
    {
        elements = new String[ DEFAULT_CAPACITY ];
        size = 0;
        this.growthRate = growthRate;
    }

    // list methods

    /**
     * returns how many elements are in the list.
     *
     * @return the number of elements in the list
     */
    @Override
    public int size()
    {
        return size;
    }

    /**
     * empties the whole list.
     */
    @Override
    public void clear()
    {
        // start over with a fresh array
        elements = new String[ DEFAULT_CAPACITY ];
        size = 0;
    }

    /**
     * adds an element at the given spot and shifts everything after it over.
     *
     * @param index  where to put it, 0 up to size
     * @param toAdd  the string to add, can't be null
     * @return true if it got added
     * @throws NullPointerException if toAdd is null
     * @throws IndexOutOfBoundsException if index is below 0 or past size
     */
    @Override
    public boolean add( int index, String toAdd ) throws NullPointerException, IndexOutOfBoundsException
    {
        if ( toAdd == null )
        {
            throw new NullPointerException();
        }
        // index == size is allowed, that just means add to the end
        if ( index < 0 || index > size )
        {
            throw new IndexOutOfBoundsException();
        }

        // make room if the array is full
        if ( size == elements.length )
        {
            grow();
        }

        // shift everything from index onward one spot right
        for ( int i = size; i > index; i-- )
        {
            elements[ i ] = elements[ i - 1 ];
        }

        elements[ index ] = toAdd;
        size++;
        return true;
    }

    /**
     * adds an element to the end of the list.
     *
     * @param toAdd  the string to add, can't be null
     * @return true if it got added
     * @throws NullPointerException if toAdd is null
     */
    @Override
    public boolean add( String toAdd ) throws NullPointerException
    {
        // adding at index size is the same as adding to the end
        return add( size, toAdd );
    }

    /**
     * adds everything from another list to the end of this one, in order.
     *
     * @param toAdd  the list to copy from, can't be null
     * @return true if everything got added
     * @throws NullPointerException if toAdd is null
     */
    @Override
    public boolean addAll( ListADT toAdd ) throws NullPointerException
    {
        if ( toAdd == null )
        {
            throw new NullPointerException();
        }

        // save the size first so the loop doesn't change if toAdd is this list
        int count = toAdd.size();
        for ( int i = 0; i < count; i++ )
        {
            add( toAdd.get( i ) );
        }
        return true;
    }

    /**
     * returns the element at the given spot.
     *
     * @param index  the spot to grab from
     * @return the element at that spot
     * @throws IndexOutOfBoundsException if index is below 0 or not less than size
     */
    @Override
    public String get( int index ) throws IndexOutOfBoundsException
    {
        checkIndex( index );
        return elements[ index ];
    }

    /**
     * removes the element at the given spot and shifts everything after it back.
     *
     * @param index  the spot to remove from
     * @return the element that got removed
     * @throws IndexOutOfBoundsException if index is below 0 or not less than size
     */
    @Override
    public String remove( int index ) throws IndexOutOfBoundsException
    {
        checkIndex( index );
        String removed = elements[ index ];

        // shift everything after index one spot left to close the gap
        for ( int i = index; i < size - 1; i++ )
        {
            elements[ i ] = elements[ i + 1 ];
        }

        elements[ size - 1 ] = null;
        size--;
        return removed;
    }

    /**
     * removes the first match of the given element.
     *
     * @param toRemove  the string to remove, can't be null
     * @return the removed element, or null if it wasn't in the list
     * @throws NullPointerException if toRemove is null
     */
    @Override
    public String remove( String toRemove ) throws NullPointerException
    {
        int index = indexOf( toRemove );

        // not in the list
        if ( index == -1 )
        {
            return null;
        }
        return remove( index );
    }

    /**
     * replaces the element at the given spot.
     *
     * @param index     the spot to replace
     * @param toChange  the new string, can't be null
     * @return the old element that was there
     * @throws NullPointerException if toChange is null
     * @throws IndexOutOfBoundsException if index is below 0 or not less than size
     */
    @Override
    public String set( int index, String toChange ) throws NullPointerException, IndexOutOfBoundsException
    {
        if ( toChange == null )
        {
            throw new NullPointerException();
        }
        checkIndex( index );

        String old = elements[ index ];
        elements[ index ] = toChange;
        return old;
    }

    /**
     * checks if there's nothing in the list.
     *
     * @return true if the list is empty, false if not
     */
    @Override
    public boolean isEmpty()
    {
        return size == 0;
    }

    /**
     * checks if the element is somewhere in the list.
     *
     * @param toFind  the string to look for, can't be null
     * @return true if it's in the list, false if not
     * @throws NullPointerException if toFind is null
     */
    @Override
    public boolean contains( String toFind ) throws NullPointerException
    {
        return indexOf( toFind ) != -1;
    }

    /**
     * finds the spot of the first match.
     *
     * @param toFind  the string to look for, can't be null
     * @return the index of the first match, or -1 if it's not there
     * @throws NullPointerException if toFind is null
     */
    @Override
    public int indexOf( String toFind ) throws NullPointerException
    {
        if ( toFind == null )
        {
            throw new NullPointerException();
        }

        for ( int i = 0; i < size; i++ )
        {
            if ( elements[ i ].equals( toFind ) )
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * copies the list into the given array in order. if the array's too small,
     * a new one that fits is made instead.
     *
     * @param toHold  the array to copy into, can't be null
     * @return an array with everything in the list
     * @throws NullPointerException if toHold is null
     */
    @Override
    public String[] toArray( String[] toHold ) throws NullPointerException
    {
        if ( toHold == null )
        {
            throw new NullPointerException();
        }

        // too small, so make one that fits
        if ( toHold.length < size )
        {
            toHold = new String[ size ];
        }

        System.arraycopy( elements, 0, toHold, 0, size );
        return toHold;
    }

    /**
     * gives back an iterator that goes through the list from front to back.
     *
     * @return a new iterator for the list
     */
    @Override
    public Iterator iterator()
    {
        return new ArrayListIterator();
    }

    // helper methods

    /**
     * makes the array bigger by the growth rate.
     */
    private void grow()
    {
        // grow by at least 1 so a small growth rate still makes room
        int growBy = Math.max( 1, (int) ( elements.length * growthRate ) );
        elements = Arrays.copyOf( elements, elements.length + growBy );
    }

    /**
     * makes sure the index points at an actual element.
     *
     * @param index  the index to check
     * @throws IndexOutOfBoundsException if index is below 0 or not less than size
     */
    private void checkIndex( int index ) throws IndexOutOfBoundsException
    {
        if ( index < 0 || index >= size )
        {
            throw new IndexOutOfBoundsException();
        }
    }

    // iterator

    /**
     * goes through the list from front to back.
     */
    private class ArrayListIterator implements Iterator
    {
        /** spot of the next element to return */
        private int cursor = 0;

        /**
         * checks if there's still more to go through.
         *
         * @return true if there's a next element, false if not
         */
        @Override
        public boolean hasNext()
        {
            return cursor < size;
        }

        /**
         * returns the next element and moves forward.
         *
         * @return the next element in the list
         * @throws NoSuchElementException if there's nothing left
         */
        @Override
        public String next() throws NoSuchElementException
        {
            if ( !hasNext() )
            {
                throw new NoSuchElementException();
            }
            return elements[ cursor++ ];
        }
    }
}