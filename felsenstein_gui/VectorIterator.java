
import java.util.*;

/** 
 * Class for iterating through the elements of a vector (not
 * included in Java 1.1)
 * 
 * @author Anders Mikkelsen
 */
public class VectorIterator
{
    /** 
     * Current position in the vector
     */
    protected int iterate_index;

    /** 
     * The iterated vector
     */
    protected Vector vector;
    
    /** 
     * Make a new vector iterator
     * 
     * @param v 
     */
    public VectorIterator(Vector v) { vector = v; iterate_index = 0; }
    
    /** 
     * Has the vector more elements?
     * 
     * @return true if the vector has more elements, false otherwise
     */
    public boolean hasNext() { return iterate_index < vector.size(); }

    /** 
     * Rewind the iterator
     * 
     */
    public void rewind() { iterate_index = 0; }

    /** 
     * Get the next element, set current to next element
     * 
     * @return the next element in the iteration
     */
    public Object next() 
    { 
	if (iterate_index < vector.size())
	    return vector.elementAt(iterate_index++);
	return null;
    }
}
