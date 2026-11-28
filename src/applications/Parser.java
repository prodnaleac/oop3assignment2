package applications;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import exceptions.EmptyQueueException;
import implementations.MyArrayList;
import implementations.MyStack;
import implementations.MyQueue;

/**
 * Checks a text file for matched and nested brackets using {@link MyStack}
 * and {@link MyQueue}. Run as {@code java -jar Parser.jar <file>}.
 */
public class Parser
{
    /** separator between the bracket and its line number, e.g. "(|12" */
    private static final String SEPARATOR = "|";

    /** every character the parser cares about, everything else is ignored */
    private static final String BRACKETS = "(){}[]<>";

    /**
     * runs the parser on the file in args[0].
     *
     * @param args the command-line arguments, args[0] has to be the path of the file to parse
     */
    public static void main( String[] args )
    {
        // missing argument
        if ( args == null || args.length == 0 )
        {
            System.out.println( "Error: no file was given." );
            System.out.println( "Usage: java -jar Parser.jar <path to text file>" );
            return;
        }

        String fileName = args[ 0 ];
        File file = new File( fileName );

        // file not found
        if ( !file.exists() )
        {
            System.out.println( "Error: the file \"" + fileName + "\" could not be found." );
            return;
        }

        try
        {
            parseFile( file );
        }
        catch ( IOException e )
        {
            System.out.println( "Error: the file \"" + fileName + "\" could not be read (" + e.getMessage() + ")." );
        }
        catch ( EmptyQueueException e )
        {
            System.out.println( "Error: the parser hit an unexpected empty queue (" + e.getMessage() + ")." );
        }
    }

    /**
     * reads the file, sends each bracket to the main loop, then prints the errors.
     *
     * @param file the text file to parse, has to exist
     * @throws IOException if the file can't be opened or read
     * @throws EmptyQueueException if a queue is read while empty
     */
    private static void parseFile( File file ) throws IOException, EmptyQueueException
    {
        MyStack bracketStack = new MyStack();
        MyQueue errorQ = new MyQueue();
        MyQueue extrasQ = new MyQueue();

        // keep every line so errors can show what the line looked like
        MyArrayList lines = new MyArrayList();

        try ( BufferedReader reader = new BufferedReader( new FileReader( file ) ) )
        {
            String line;
            int lineNumber = 0;

            while ( ( line = reader.readLine() ) != null )
            {
                lineNumber++;
                lines.add( line );

                // pull out each bracket and tag it with its line number
                for ( int i = 0; i < line.length(); i++ )
                {
                    char ch = line.charAt( i );

                    // skip < and > when they're used as comparison operators
                    if ( BRACKETS.indexOf( ch ) != -1 && !isComparison( line, i ) )
                    {
                        processBracket( ch + SEPARATOR + lineNumber, bracketStack, errorQ, extrasQ );
                    }
                }
            }
        }

        // empty file
        if ( lines.isEmpty() )
        {
            System.out.println( "The file \"" + file.getName() + "\" is empty, there is nothing to parse." );
            return;
        }

        System.out.println( "Parsing \"" + file.getName() + "\" (" + lines.size() + " lines)" );
        System.out.println( "--------------------------------------------------" );

        MyArrayList errors = checkEndOfFile( bracketStack, errorQ, extrasQ );

        // print errors in the order they show up in the file
        long sortingStart = System.nanoTime();
        sortByLineNumber( errors );
        long sortingEnd = System.nanoTime();
        double sortingTimeMs = ( sortingEnd - sortingStart ) / 1_000_000.0;

        System.out.println( "Sorting time: " + sortingTimeMs + " ms" );

        for ( int i = 0; i < errors.size(); i++ )
        {
            reportError( errors.get( i ), lines );
        }

        int errorCount = errors.size();

        System.out.println( "--------------------------------------------------" );

        if ( errorCount == 0 )
        {
            System.out.println( "No errors found, every bracket is matched and nested correctly." );
        }
        else
        {
            System.out.println( errorCount + " error(s) found." );
        }
    }

    /**
     * empties the stack into errorQ, then compares
     * errorQ with extrasQ.
     *
     * @param bracketStack leftover opening brackets
     * @param errorQ possible errors from the main loop
     * @param extrasQ closing brackets with no opener
     * @return every error as a "bracket|line" entry
     * @throws EmptyQueueException if a queue is read while empty
     */
    private static MyArrayList checkEndOfFile( MyStack bracketStack, MyQueue errorQ, MyQueue extrasQ )
            throws EmptyQueueException
    {
        MyArrayList errors = new MyArrayList();

        // if the stack is not empty, pop each one into errorQ
        while ( !bracketStack.isEmpty() )
        {
            errorQ.enqueue( bracketStack.pop() );
        }

        // repeat until both queues are empty
        while ( !errorQ.isEmpty() || !extrasQ.isEmpty() )
        {
            // only one queue has anything left, so everything in it is an error
            if ( extrasQ.isEmpty() )
            {
                errors.add( errorQ.dequeue() );
            }
            else if ( errorQ.isEmpty() )
            {
                errors.add( extrasQ.dequeue() );
            }

            // both have something, so peek both and compare the bracket characters
            else if ( errorQ.peek().charAt( 0 ) != extrasQ.peek().charAt( 0 ) )
            {
                errors.add( errorQ.dequeue() );
            }
            else
            {
                // they cancel each other out
                errorQ.dequeue();
                extrasQ.dequeue();
            }
        }

        return errors;
    }

    /**
     * sorts the errors by line number (insertion sort).
     *
     * @param errors the "bracket|line" entries to sort
     */
    private static void sortByLineNumber( MyArrayList errors )
    {
        for ( int i = 1; i < errors.size(); i++ )
        {
            String current = errors.get( i );
            int j = i - 1;

            // shift bigger line numbers one spot right to make room
            while ( j >= 0 && lineOf( errors.get( j ) ) > lineOf( current ) )
            {
                errors.set( j + 1, errors.get( j ) );
                j--;
            }

            errors.set( j + 1, current );
        }
    }

    /**
     * pulls the line number out of a "bracket|line" entry.
     *
     * @param entry the entry, e.g. "(|12"
     * @return the line number, e.g. 12
     */
    private static int lineOf( String entry )
    {
        return Integer.parseInt( entry.split( "\\|" )[ 1 ] );
    }

    /**
     * prints one error with its line number and line text.
     *
     * @param entry the bracket and its line number, e.g. "(|12"
     * @param lines every line of the file
     */
    private static void reportError( String entry, MyArrayList lines )
    {
        String[] parts = entry.split( "\\|" );
        char bracket = parts[ 0 ].charAt( 0 );
        int lineNumber = lineOf( entry );

        String problem = isOpening( bracket )
                ? "opening '" + bracket + "' is never closed"
                : "closing '" + bracket + "' has no matching opening bracket";

        System.out.println( "Error at line " + lineNumber + ": " + problem );
        System.out.println( "    " + lines.get( lineNumber - 1 ).trim() );
    }

    /**
     * checks if a character is one of the four opening brackets.
     *
     * @param bracket the character to check
     * @return true if it is ( { [ or <, false if not
     */
    private static boolean isOpening( char bracket )
    {
        return bracket == '(' || bracket == '{' || bracket == '[' || bracket == '<';
    }

    /**
     * checks if the < or > at the given spot is a comparison instead of a bracket:
     * <= >=, or a lone < / > with spaces on both sides sitting between
     * two values (like "a < b").
     *
     * @param line the line to check
     * @param index the spot of the character to check
     * @return true if it's a comparison, false if it's a bracket
     */
    private static boolean isComparison( String line, int index )
    {
        char ch = line.charAt( index );

        if ( ch != '<' && ch != '>' )
        {
            return false;
        }

        char before = ( index > 0 ) ? line.charAt( index - 1 ) : ' ';
        char after = ( index < line.length() - 1 ) ? line.charAt( index + 1 ) : ' ';

        // <= >=
        if ( after == '=' )
        {
            return true;
        }

        // a lone < or > with spaces around it, between two values
        if ( Character.isWhitespace( before ) && Character.isWhitespace( after ) )
        {
            char prevValue = nextNonSpace( line, index - 1, -1 );
            char nextValue = nextNonSpace( line, index + 1, 1 );

            boolean valueBefore = Character.isLetterOrDigit( prevValue ) || prevValue == '_'
                    || prevValue == ')' || prevValue == ']';

            boolean valueAfter = Character.isLetterOrDigit( nextValue ) || nextValue == '_'
                    || nextValue == '(' || nextValue == '-';

            return valueBefore && valueAfter;
        }

        return false;
    }

    /**
     * finds the first character that isn't whitespace, walking from a start spot.
     *
     * @param line the line to search
     * @param start the spot to start from
     * @param step 1 to walk right, -1 to walk left
     * @return the first non-space character, or a space if there isn't one
     */
    private static char nextNonSpace( String line, int start, int step )
    {
        for ( int i = start; i >= 0 && i < line.length(); i += step )
        {
            if ( !Character.isWhitespace( line.charAt( i ) ) )
            {
                return line.charAt( i );
            }
        }

        return ' ';
    }

    /**
     * runs one bracket through the main loop of the parser algorithm.
     *
     * @param bracketEntry the bracket and its line number, e.g. "(|11"
     * @param bracketStack the stack of opening brackets
     * @param errorQ the queue of possible errors
     * @param extrasQ the queue of closing brackets with no opener
     * @throws EmptyQueueException if errorQ is read while empty
     */
    public static void processBracket( String bracketEntry, MyStack bracketStack, MyQueue errorQ,
            MyQueue extrasQ ) throws EmptyQueueException
    {
        char bracket = bracketEntry.charAt( 0 );

        // opening bracket: push on stack
        if ( bracket == '(' || bracket == '{' || bracket == '[' || bracket == '<' )
        {
            bracketStack.push( bracketEntry );
        }

        // closing bracket
        else if ( bracket == ')' || bracket == '}' || bracket == ']' || bracket == '>' )
        {
            // matches the top of the stack, so pop and all is well
            if ( !bracketStack.isEmpty() && isMatchingPair( bracketStack.peek().charAt( 0 ), bracket ) )
            {
                bracketStack.pop();
            }

            // matches the head of errorQ, so dequeue and ignore
            else if ( !errorQ.isEmpty() && isMatchingPair( errorQ.peek().charAt( 0 ), bracket ) )
            {
                errorQ.dequeue();
            }

            // stack is empty, so add to errorQ
            else if ( bracketStack.isEmpty() )
            {
                errorQ.enqueue( bracketEntry );
            }

            // otherwise, search the stack for a matching opening bracket
            else
            {
                MyStack tempStack = new MyStack();
                boolean foundMatch = false;

                while ( !bracketStack.isEmpty() )
                {
                    String entry = bracketStack.pop();

                    if ( isMatchingPair( entry.charAt( 0 ), bracket ) )
                    {
                        foundMatch = true;
                        break;
                    }

                    tempStack.push( entry );
                }

                if ( foundMatch )
                {
                    // everything above the matching opener is an error
                    while ( !tempStack.isEmpty() )
                    {
                        errorQ.enqueue( tempStack.pop() );
                    }
                }
                else
                {
                    // no match, so put the stack back the way it was
                    while ( !tempStack.isEmpty() )
                    {
                        bracketStack.push( tempStack.pop() );
                    }

                    // no matching opener, so add the closer to extrasQ
                    extrasQ.enqueue( bracketEntry );
                }
            }
        }
    }

    /**
     * checks if an opening bracket matches a closing bracket.
     *
     * @param opening the opening bracket
     * @param closing the closing bracket
     * @return true if the brackets match, false if not
     */
    private static boolean isMatchingPair( char opening, char closing )
    {
        return ( opening == '(' && closing == ')' )
                || ( opening == '{' && closing == '}' )
                || ( opening == '[' && closing == ']' )
                || ( opening == '<' && closing == '>' );
    }
}