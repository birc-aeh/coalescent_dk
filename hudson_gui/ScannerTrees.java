
import java.io.*;
import java.util.*;

/**
 * A set of extracted trees (called scanner trees - historical reasons)
 *
 * @author Anders Mikkelsen
 */
public class ScannerTrees extends Vector
{
    /// Height of highest tree
    protected double max_height;

    /// Are these trees extracted from a model with migration
    protected boolean migration;

    /**
     * Get the heighest tree
     * 
     * @return height of highest tree
     */
    private double getMaxHeight() { return max_height; }


    /**
     * Make set of extracted trees based on trees in serialized form
     * 
     * @param in source of serialized trees
     * @param migration is this a migration setting
     */
    public ScannerTrees(Vector v, boolean migration) throws IOException, TreeFormatException
    {
	// constructor changed
	// using Vector of Strings instead of BufferedReader
	// for debugging purposes
	// Lasse Westh-Nielsen - 24/9/2002

	super();

	this.migration = migration;

	max_height = 0.0;
	String s;

	while (v.size() > 0)
	    {
		s = (String) v.elementAt(0);

		v.removeElementAt(0);

		parseLine(s);
	    }

	//while ((s = in.readLine()) != null) parseLine(s);

	VectorIterator vi = new VectorIterator(this);
	while (vi.hasNext())
	    ((AnimationTree)(vi.next())).setHeightScale(max_height);
    }

    /**
     * Tokenize a string
     *
     * @param s string to tokenize
     * @param delim delimiter
     * @param num expected number of tokens
     */
    private String[] getTokens(String s, String delim, int num) throws TreeFormatException
    {
	String ret[] = new String[num];
	StringTokenizer st = new StringTokenizer(s,delim,false);
	int i;

	i = 0;
	while (st.hasMoreTokens()) {
	    if (i == num) 
		throw new TreeFormatException("Bad tree format : trying to get " + num + " tokens from " + s);
	    ret[i++] = st.nextToken();	    
	}
	return ret;
    }


    /**
     * Parse one input line - ie. de-serialize one tree
     * 
     * @param s the serialized tree
     */
    private void parseLine(String s) throws TreeFormatException
    {
	String arg[] = getTokens(s,"|",2);
	double time;
	ScannerTree t;

	try {
	    time = (new Double(arg[0])).doubleValue();
	} catch (Exception e) {
	    throw new TreeFormatException("Scanner trees must begin with a position");
	}

	t = new ScannerTree(time,arg[1]);
	max_height = Math.max(max_height,t.getHeight());
	addElement(new AnimationTree(t,migration));
    }


}
