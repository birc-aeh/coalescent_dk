
import java.util.*;

/**
 * Small class for iterating through the elements of a Vector
 *
 * @author Anders Mikkelsen
 */
public class VectorIterator
{
    /// Current element
    protected int iterate_index;

    /// The vector to iterate over
    protected Vector vector;
    
    /**
     * Create new iterator on vector
     *
     * @param v the vector to iterate over
     */
    public VectorIterator(Vector v) { vector = v; iterate_index = 0; }
    
    /**
     * Does the vector have more elements
     *
     * @return true if there are more elements in the vector
     */
    public boolean hasNext() { return iterate_index < vector.size(); }

    /**
     * Start over the iteration
     */
    public void rewind() { iterate_index = 0; }

    /**
     * Get the next element
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
