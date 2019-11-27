
/**
 * Node of an extracted tree (called a scanner tree - historical reasons)
 *
 * @author Anders Mikkelsen
 */
public class ScannerTreeNode implements CanCompare
{

    /// Siblings
    public ScannerTreeNode left,right;

    /// Height
    public double time;

    /// Indetifier
    public int ID;


    /**
     * New tree
     * 
     * @param time height of node
     */
    public ScannerTreeNode(double time)
    {
	this.time = time;
	this.left = null;
	this.right = null;
	this.ID = -1;
    }

    /**
     * Compare nodes - used for sorting on height
     */
    public int compareTo(Object o)
    {
	double t = ((ScannerTreeNode)o).time;
	
	if (time<t) return -1;
	if (time==t) return 0;
	return 1;
    }

}
