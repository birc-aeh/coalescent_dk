
/**
 * Classes that believe that they can compare themselves to
 * themselves ought to implement this interface.
 *
 * @author Anders Mikkelsen
 */
public interface CanCompare
{
    /**
     * Compare this to another object of the same type
     *
     * @param o the object to compare this to
     * @return -1 if this object is smaller than o, 0 if the are equal, and 1 of o is the smaller
     */
    public int compareTo(Object o);
}
