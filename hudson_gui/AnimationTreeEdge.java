
/**
 * Edges in the animation tree. 
 *
 * @author Anders Mikkelsen
 */
public class AnimationTreeEdge
{
    /// Edge endpoint
    AnimationTreeNode src, dest;

    /// Edge ID
    int id;

    /// ID of edge endpoints
    int src_id, dest_id;

    /// Intervals associated with edge
    IntervalList intervals;

    /// Grey intervals - currently unused, but used to indicate what parts have found MRCA
    IntervalList grey_intervals;

    /// Has this edge got its own coordinates (used in the migration animation)
    boolean has_coord;

    /// Coordinate in case the edge has its own coordinates
    int x,y;

    /// True if this edge has a selection mutation
    boolean hasMutation;

    /// True if this edge is selected by selection rules
    boolean hasSelection;

    /**
     * Make edge with indicated source and destination
     *
     * @param id   id of this new edge
     * @param src  the source of the edge
     * @param dest the destination of the edge
     */
    public AnimationTreeEdge(int id, AnimationTreeNode src, AnimationTreeNode dest)
    {
	this.id = id;
	this.src = src;
	this.dest = dest;
	has_coord = false;
	hasMutation = false;
	hasSelection = false;
    }

    /**
     * Make edge on ID's only
     *
     * @param id  id of this new edge
     * @param src the source of the edge
     * @param dest the destination of the edge
     */
    public AnimationTreeEdge(int id, int src, int dest)
    {
	src_id = src;
	dest_id = dest;
	this.id = id;
	has_coord = false;
    }

    /**
     * Get the ID of this edge
     *
     * @return id of this edge
     */
    public int getID() { return id; }


    /**
     * Set intervals for this edge
     *
     * @param il the intervals to associate with this edge
     */
    public void setIntervals(IntervalList il) { intervals = il; }


    /**
     * Get intervals for this edge
     *
     * @return intervals for this edge
     */
    public IntervalList getIntervals() { return intervals; }

    /**
     * Set grey intervals for this edge
     *
     * @param il the set of intervals
     */
    public void setGreyIntervals(IntervalList il) { grey_intervals = il; }

    /**
     * Get the grey intervals for this edge (ie. the intervals that have
     * found MRCA along at this point).
     *
     * @return the grey intervals
     */
    public IntervalList getGreyIntervals() { return grey_intervals; }

    /**
     * Does this edge match supplied ID's
     *
     * @param src source of query
     * @param dest destination of query
     * @return if this edge matches (src,dest)
     */
    public boolean match(int src, int dest)
    {
	return (src_id == src) && (dest_id == dest);
    }

    /**
     * @return id of source node
     */
    public int getSrcID() { return src_id; }
    
    /**
     * @return id of destination node
     */
    public int getDestID() { return dest_id; }
     

}
