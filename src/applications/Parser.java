package applications;

import implementations.MyStack;
import implementations.MyQueue;

/**
 * Handles the bracket-processing portion of the parser algorithm.
 */
public class Parser
{
    /**
     * Processes a bracket according to Kitty's parser algorithm.
     *
     * @param bracketEntry the bracket and its line number (e.g. "(|12")
     * @param bracketStack the stack of opening brackets
     * @param errorQ the queue of potential syntax errors
     * @param extrasQ the queue of unmatched closing brackets
     */
    public static void processBracket(String bracketEntry,
            MyStack bracketStack, MyQueue errorQ, MyQueue extrasQ)
    {
        char bracket = bracketEntry.charAt(0);

        // Opening bracket: push on stack
        if (bracket == '(' || bracket == '{'
                || bracket == '[' || bracket == '<')
        {
            bracketStack.push(bracketEntry);
        }

        // Closing bracket
        else if (bracket == ')' || bracket == '}'
                || bracket == ']' || bracket == '>')
        {
            // If it matches the top of the stack, pop and all is well
            if (!bracketStack.isEmpty()
                    && isMatchingPair(bracketStack.peek().charAt(0), bracket))
            {
                bracketStack.pop();
            }

            // Else if it matches the head of errorQ, dequeue and ignore
            else if (!errorQ.isEmpty()
                    && isMatchingPair(errorQ.peek().charAt(0), bracket))
            {
                errorQ.dequeue();
            }

            // Else if stack is empty, add to errorQ
            else if (bracketStack.isEmpty())
            {
                errorQ.enqueue(bracketEntry);
            }

            // Otherwise, search stack for matching opening bracket
            else
            {
                MyStack tempStack = new MyStack();
                boolean foundMatch = false;

                // Search the stack
                while (!bracketStack.isEmpty())
                {
                    String entry = bracketStack.pop();

                    if (isMatchingPair(entry.charAt(0), bracket))
                    {
                        foundMatch = true;
                        break;
                    }

                    tempStack.push(entry);
                }

                if (foundMatch)
                {
                    // Entries above the matching opener are errors
                    while (!tempStack.isEmpty())
                    {
                        errorQ.enqueue(tempStack.pop());
                    }
                }
                else
                {
                    // Restore the original stack when no match was found
                    while (!tempStack.isEmpty())
                    {
                        bracketStack.push(tempStack.pop());
                    }

                    // No matching opener: add closing bracket to extrasQ
                    extrasQ.enqueue(bracketEntry);
                }
            }
        }
    }

    /**
     * Determines whether an opening bracket matches a closing bracket.
     *
     * @param opening the opening bracket
     * @param closing the closing bracket
     * @return true if the brackets match, false otherwise
     */
    private static boolean isMatchingPair(char opening, char closing)
    {
        return (opening == '(' && closing == ')')
                || (opening == '{' && closing == '}')
                || (opening == '[' && closing == ']')
                || (opening == '<' && closing == '>');
    }
}