
import java.util.*;

/**
 * Vector of intervals and breakpoints (recombination points etc.)
 *
 * @author Anders Mikkelsen
 */
public class IntervalList extends Vector
{
    /// The empty constructor
    public IntervalList() { };

    /// Vector of breakpoints
    protected Vector breakPoints;

    /**
     * Make interval list by parsing intervals in a string
     *
     * @param s string with serialized intervals and breakpoints
     */
    public IntervalList(String s) throws TreeFormatException
    {
	super();
	
	breakPoints = new Vector();
	if (s==null) return;

	StringTokenizer st = new StringTokenizer(s,"#",false);
	int comma;
	while (st.hasMoreTokens()) {
	    String iv = st.nextToken();
	    if (iv.equals(" ")) continue;
	    for (comma=0; comma<iv.length(); comma++)
		if (iv.charAt(comma) == ',') break;
	    if (comma==0)
		throw new TreeFormatException("Bad Interval");

	    if (comma==iv.length()) {
		try {		    
		    breakPoints.addElement(new Double(iv));
		} catch (Exception e) {
		    throw new TreeFormatException("Bad Interval");
		}
	    } else { 
		try {
		    addElement(new Interval((new Double(iv.substring(0,comma))).doubleValue(),
					    (new Double(iv.substring(comma+1))).doubleValue()));
		} catch (Exception e) {
		    throw new TreeFormatException("Bad Interval");
		}
	    } 	
	}
    }

    /**
     * Get vector of breakpoints
     *
     * @return vector of breakpoints
     */
    public Vector getBreakPoints() { return breakPoints; }

}
