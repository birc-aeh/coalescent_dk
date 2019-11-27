
import java.util.*;

/**
 * A vector capable bubble sorting its elements. Elements
 * are required to implement the CanCompare interface.
 *
 * @author Anders Mikkelsen
 * @see CanCompare
 */
public class SortingVector extends Vector
{
    /**
     * Swap elements
     *
     * @param a index of first element
     * @param b index of second element
     */    
    private void swapElements(int a, int b)
    {
	Object tmp = elementAt(a);
	setElementAt(elementAt(b),a);
	setElementAt(tmp,b);
    }

    /**
     * Sort elements in the vector
     */
    public void bubbleSort()
    {
	int i = 1;
	while (i<size()) {
	    CanCompare a = (CanCompare)(elementAt(i-1));
	    int rv = a.compareTo(elementAt(i));

	    if (rv <= 0) { i++; continue; }
	    
	    swapElements(i-1,i);
	    i = Math.max(1,i-1);
	}
    }
}
