
import java.util.*;
import java.io.*;

/**
 * A small representation of an extracted tree. ScannerTrees are converted
 * to animation trees once created. This tree is introduced for simplicity?
 *
 * @author Anders Mikkelsen
 */
public class ScannerTree
{
    /// Where does this tree belong
    protected double position;

    /// The root node of the tree
    protected ScannerTreeNode root;

    /// List of nodes in the tree
    protected SortingVector nodelist;

    /// Number of leaves in the tree
    public int leaves;

    
    /**
     * Make new extracted tree
     *
     * @param pos position of tree (start point)
     * @param tree serialized representation of a tree
     */
    public ScannerTree(double pos, String tree) throws TreeFormatException
    {
	int i;

	this.position = pos;

	StringTokenizer st = new StringTokenizer(tree," ",false);
	
	nodelist = new SortingVector();
	root = parseTree(st);

	nodelist.bubbleSort();	
	VectorIterator vi = new VectorIterator(nodelist);
	i = 0;
	leaves = 0;
	while (vi.hasNext()) {
	    ScannerTreeNode n = (ScannerTreeNode)(vi.next());
	    if (n.ID == -1) n.ID = i;
	    else leaves++;
	    i++;
	}
    }

    /**
     * Get height of tree
     * 
     * @return height of tree
     */
    public double getHeight() { return root.time; }

    /**
     * Recursively construct the tree
     *
     * @param st tokenized serialized input
     * @return root of tree
     */
    private ScannerTreeNode parseTree(StringTokenizer st) throws TreeFormatException
    {
	ScannerTreeNode n = null;

	if (!(st.hasMoreTokens()))
	    throw new TreeFormatException("Problems reading scanner tree");

	try {
	    String s = st.nextToken();
	    if (s.indexOf('.') == -1) {
		n = new ScannerTreeNode(0.0);
		n.ID = (new Integer(s)).intValue();
	    } else {
		n = new ScannerTreeNode((new Double(s)).doubleValue());
	    }
	} catch (Exception e) {
	    throw new TreeFormatException("Bad scanner tree format:" + e);
	}	

	if (n.time > 0.0) {
	    n.left = parseTree(st);
	    n.right = parseTree(st);
	}

	nodelist.addElement(n);
	return n;
    }
    
    /**
     * Get position of this tree
     *
     * @return position of tree
     */
    public double getPos() { return position; }

    /**
     * Get root node
     *
     * @return root node
     */
    public ScannerTreeNode getRoot() { return root; }

    /**
     * Get nodes as a list (sorted on height)
     *
     * @return nodes as a list
     */
    public Vector getNodeList() { return nodelist; }

}
