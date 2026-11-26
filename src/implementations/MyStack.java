package implementations;
import utilities.Iterator;

import java.util.EmptyStackException;
import utilities.StackADT;

/**
 * An array-backed implementation of the {@link StackADT} interface,
 * for use in CPRG 304 at SAIT.
 * <p>
 * This class delegates all storage to an internal {@link MyArrayList},
 * treating the end of the list as the top of the stack. {@link #push(String)}
 * appends to the end; {@link #pop()} and {@link #peek()} access the last
 * element. A stack follows a <strong>Last-In, First-Out (LIFO)</strong>
 * ordering policy: the last element pushed is the first one returned.
 * </p>
 *
 * <p><strong>Design Note – String-Specific Implementation:</strong><br>
 * This class is intentionally designed to store {@link String} elements only.
 * A fully reusable, type-safe design would use Java generics (e.g.,
 * {@code MyStack<E>}), allowing any object type to be stored in the stack.
 * Generic programming is outside the scope of CPRG 304 and has therefore
 * been omitted. All element fields, parameters, and return types are fixed
 * as {@link String} throughout this class and its implementations.
 * </p>
 */
public class MyStack implements StackADT
{
	/** 
	 * the underlying list used to store the stack elements.
	 */
	private MyArrayList stack;
	
	/**
	 * constructs an empty stack
	 */
	public MyStack() 
	{
		stack = new MyArrayList();
		
	}
	
	/**
	 * pushes the specified element onto the top of the stack.
	 * 
	 * @param toAdd the String element to add
	 * @throws NullPointerException if toAdd is null
	 */
	@Override
	public void push(String toAdd) throws NullPointerException
	{
		stack.add(toAdd);
	}
	
	
	/**
	 * Removes and returns the element at the top of the stack
	 * 
	 * @return the String removed from the top of the stack
	 * @throws EmptyStackException if the stack is empty
	 */
	@Override
	public String pop() throws EmptyStackException
	{
		if (stack.isEmpty())
		{
			throw new EmptyStackException();	
		}
		
		return stack.remove(stack.size()-1);
	}
	
	/**
	 * Returns the element at the top of the stack without removing it
	 * 
	 * @return the String at the top of the stack
	 * @throws EmptyStackException if stack is empty
	 */
	@Override
	public String peek() throws EmptyStackException
	{
		if(stack.isEmpty())
		{
			throw new EmptyStackException();
		}
		
		return stack.get(stack.size()-1);
	}
	
	/**
	 * Removes all elements from the stack
	 */
	@Override
	public void clear()
	{
		stack.clear();
	}
	
	/**
	 * checks whether the stack contains no elements
	 * 
	 * @return true if the stack is empty, false otherwise
	 */
	@Override
	public boolean isEmpty()
	{
		return stack.isEmpty();	
	}
	
	/**
	 * copies the stack elements into the provided array in the bottom-to-top order
	 * 
	 * @param holder the array into which the stack elements will be copied
	 * @return an array containing the stack elements
	 * @throws NullPointerException if holder is null
	 */
	@Override
	public String[] toArray(String[] holder) throws NullPointerException
	{
		return stack.toArray(holder);
	}
	
	/**
	 * Checks whether the specified element exists in the stack
	 * 
	 * @param toFind the String to search for
	 * @return true if the element exist, false otherwise
	 * @throws NullPointerException if toFind is null
	 */
	@Override
	public boolean contains(String toFind) throws NullPointerException
	{
		return stack.contains(toFind);
	}

	/** 
	 * searches for the specified element from the top of the stack
	 * 
	 * @param toFind the String to search for
	 * @return the 1-based position from the top, or -1 if not found
	 */
	@Override
	public int search(String toFind)
	{
		for (int i = stack.size() - 1; i >=0; i--)
		{
			if (toFind != null && toFind.equals(stack.get(i)))
			{
				return stack.size() - i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Returns an iterator that traverses the stack from bottom to top
	 * 
	 * @return an iterator over the stack elements
	 */
	@Override
	public Iterator iterator()
	{
		return stack.iterator();
	}
	
	/**
	 * Compares this stack with another stack for equality
	 * 
	 * @param that the stack to compare with
	 * @return true if both stacks contain the same elements in the same order
	 */
	@Override 
	public boolean equals(StackADT that)
	{
		if (that == null || stack.size() != that.size())
		{
			return false;		
		}
		
		String[] thisElements = stack.toArray(new String[stack.size()]);
		String[] thatElements = that.toArray(new String[stack.size()]);
		
		for (int i = 0; i < thisElements.length; i++)
		{
			if(!thisElements[i].equals(thatElements[i]))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Returns the number of elements in the stack
	 * 
	 * @return the number of elements currently in the stack
	 */
	@Override
	public int size()
	{
		return stack.size();
	}
	
}