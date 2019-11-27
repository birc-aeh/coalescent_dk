
import java.util.*;

/**
 * A node in an animation tree
 *
 * @author Anders Mikkelsen
 */
public class AnimationTreeNode
{
    /// ID of this node
    public int ID;
    
    /// Colour of this node
    public int color;

    /// Height of this node
    public double time;

    /// Description of this node
    public String description;

    /// Vector of outgoing (up-going) edges from this node - Integer ID's
    public Vector outedges;

    /// Vector of incomming (from below) edges
    public Vector ins;

    /// Vector of outgoing (up-going) edges from this node
    public Vector outs;

    /// Coordinate of this node when ready for rendering
    public int x,y;

    /// Diameter (pixels) of this node
    public int diameter;

    /// Intervals that find MRCA in this node
    public IntervalList ancestor_intervals;

    /// Intervals that still needs MRCA in this node
    public IntervalList intervals;

    /// Intervals that have found MRCA at this point
    public IntervalList grey_intervals;

    /// Is this node a migration node
    public boolean migration;

    /// What kind of selection node is this node - if the node is in a selection tree
    public char selection_type;

    /// Is the selection marks for this node currently visible
    public boolean visible_marks;


    /**
     * Create a new selection node
     *
     * @param id  id of this new node
     * @param color of this node
     * @param time time (height) of this node
     * @param desc description of this node
     */     
    public AnimationTreeNode(int id, int color, double time, String desc)
    {
	this.ID = id;
	this.color = color;
	this.time = time;
	this.description = desc;

	outedges = new Vector();
	ins = new Vector();
	outs = new Vector();
	diameter = 10;
	migration = false;
	visible_marks = false;

	try {
	    ancestor_intervals = new IntervalList((String)null);
	    intervals = new IntervalList((String)null);
	} catch (Exception e) {	}
    }

    /**
     * Add a "red" interval
     *
     * @param from left endpoint
     * @param to right endpoint
     */
    public void addInterval(double from, double to)
    {
	intervals.addElement(new Interval(from,to));
    }

    /**
     * Get the "red" intervals
     *
     * @return the intervals still needing MRCA
     */
    public IntervalList getIntervals() { return intervals; }


    /**
     * Give the complete set of red intervals
     *
     * @param l the complete set of red intervals
     */
    public void setIntervals(IntervalList l) { intervals = l; }


    /**
     * Add a "green" interval
     *
     * @param from left endpoint
     * @param to right endpoint
     */
    public void addAncestorInterval(double from, double to)
    {
	ancestor_intervals.addElement(new Interval(from,to));	
    }

    /**
     * Get the set of "green" intervals
     * 
     * @return set of intervals finding MRCA in this node
     */
    public IntervalList getAncestorIntervals() { return ancestor_intervals; }

    /**
     * Give the complete set of green intervals
     *
     * @param l the complete set of green intervals
     */
    public void setAncestorIntervals(IntervalList l) { ancestor_intervals = l; }


    /**
     * Give the complete set of grey intervals
     *
     * @param l the complete set of intervals that have found MRCA at this point
     */
    public void setGreyIntervals(IntervalList l) { grey_intervals = l; }

    /**
     * Get the grey intervals
     *
     * @return the complete set of gray intervals
     */
    public IntervalList getGreyIntervals() { return grey_intervals; }

    /**
     * Add an outedge (integer ID)
     *
     * @param succ integer id of edge to add
     */
    public void addEdge(int succ) { outedges.addElement(new Integer(succ)); }

    /**
     * Add an incomming edge
     *
     * @param n the incomming edge (from below)
     */
    public void addIn(AnimationTreeEdge n) { ins.addElement(n); }

    /**
     * Get list of incomming edges
     *
     * @return list of incomming edges
     */
    public Vector getIns() { return ins; }

    /**
     * Add an outgoing edge
     * 
     * @param n the outgoing edge (upwards)
     */
    public void addOut(AnimationTreeEdge n) { outs.addElement(n); }

    /**
     * Get outgoing edges
     *
     * @return vector of outgoing edges
     */
    public Vector getOuts() { return outs; }

    
    /**
     * Add interval to an edge going out from this node
     *
     * @param dest id of the edge to add intervals to
     * @param il   the intervals to add
     */
    public void addEdgeInterval(int dest, IntervalList il) throws TreeFormatException
    {
	VectorIterator vi = new VectorIterator(outs);
	while (vi.hasNext()) {
	    AnimationTreeEdge ate = (AnimationTreeEdge)(vi.next());
	    if (ate.dest.ID == dest) {
		ate.setIntervals(il);
		return;
	    }
	}
	throw new TreeFormatException("Cannot add edge interval to nonexisting edge");       
    }
}
