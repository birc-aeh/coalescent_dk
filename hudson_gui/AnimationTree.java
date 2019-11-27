
import java.io.*;
import java.util.*;

/**
 * The animation tree serves as a stencil for generic animated trees.
 * Trees are read in serialized form either from a BufferedReader or from
 * a string array. The serialized three generally comes from a socket
 * connection back the originating server. The connection is established by
 * calling a CGI-script on that server. The format of the serialized tree
 * is subject to constant change.
 *
 * AnimationTrees will keep an internal representation of the trees that
 * must be scaled to a specific display before the trees can be animated.
 * This is done with the <a href="#calculateCoordinates(int, int)">calculateCoordinates</a>
 * method.
 *
 * @author Anders Mikkelsen
 * @see CGIConnect
 */
public class AnimationTree
{
    /// Vector of nodes in the tree
    protected Vector nodes;
    
    /// Vector of edges in the tree
    protected Vector edges;

    /// A tree might be related to a specific position
    protected double position;

    /// Is this a migration tree
    public boolean migration;
    protected int migration_boundary;

    /// Is this a selection tree
    boolean selection;

    /// If selection, how far are we in the final "magic" steps
    int selection_step;

    /// Rendering margin
    static int margin = 10;

    /// Iterator counter
    private int nextNode;

    /// Range of intervals
    private Interval range = null;

    /// The pool of extracted trees - NOTE: ScannerTrees is a vector of AnimationTrees
    private ScannerTrees strees;

    /// Scale factor to make extracted trees have same height-ratio as the main animation tree
    private double height_scale;

    /// Random number generator
    private Random rnd;

    /// True if this tree is a scanner tree
    private boolean isScanner;


    /**
     * Init all data structures
     */
    private void initAll()
    {
	nodes = new Vector();
	edges = new Vector();
	height_scale = 0.0;
	rnd = new Random();
	isScanner = false;
	migration = false;
	selection = false;
	selection_step = 0;
    }

    /**
     * Make empty tree
     */
    protected AnimationTree()
    {
	initAll();
	range = new Interval(0.0,1.0);
    }

    /**
     * Make tree from serialized tree in BufferedReader. Closes
     * the reader when done.
     *
     * @param in source of serialized tree
     * @param l a legend listener reacting on the leged on the new tree
     */
    public AnimationTree(Vector v, LegendListener l) throws IOException, TreeFormatException
    {
	// constructor changed from:
	// AnimationTree(BufferedReader in, LegendListener l)
	// to:
	// AnimationTree(Vector in, LegendListener l)
	// for debugging purposes
	// (Vector is a collection of strings corresponding
	// to the lines of the cgi response)
	// (+ i think it will speed things up when
	// the buffer is read in one go in ParameterPanel.java?)
	// 24/9/2002 Lasse Westh-Nielsen

	String s;
	initAll();

	if (l!=null) l.clearLegend();

	while(v.size() > 0)
	    {
		s = (String) v.elementAt(0);
		
		v.removeElementAt(0);

		if (s.equals("")) break;

		// Recognize special keywords in input
		if (s.startsWith("Migration")) { migration = true; continue; }	    
		if (s.startsWith("Selection")) { selection = true; continue; }
		if (s.startsWith("L ")) { parseLegend(s.substring(2),l); continue; }
		parseLine(s);
	    }

	buildTree();

	if (selection) {
	    // Read special information on selection tree
	    s = (String) v.elementAt(0);
		
	    v.removeElementAt(0);
		
	    if (!s.startsWith("MU "))
		throw new TreeFormatException("Selection tree has no mutations");
	    parseSelectionMutations(s.substring(3),true);

	    s = (String) v.elementAt(0);
		
	    v.removeElementAt(0);

	    if (!s.startsWith("T0 "))
		throw new TreeFormatException("Selection tree has no type 0 nodes");
	    parseSelectionNodeType(s.substring(3),(char)0);

	    s = (String) v.elementAt(0);
		
	    v.removeElementAt(0);

	    if (!s.startsWith("T1 "))
		throw new TreeFormatException("Selection tree has no type 1 nodes");
	    parseSelectionNodeType(s.substring(3),(char)1);

	    s = (String) v.elementAt(0);
		
	    v.removeElementAt(0);

	    if (!s.startsWith("ME "))
		throw new TreeFormatException("Selection tree has no green edges");
	    parseSelectionMutations(s.substring(3),false);

	    s = (String) v.elementAt(0);
		
	    v.removeElementAt(0);
	}

	// Read the extracted trees
	strees = new ScannerTrees(v,migration);
	if (selection)
	    ((AnimationTree)(strees.elementAt(0))).
		setHeightScale(((AnimationTreeNode)(nodes.elementAt(nodes.size()-1))).time);

	if (range == null)
	    throw new TreeFormatException("Tree has no interval range");
    }


    /**
     * Build an animation tree from an extracted tree
     *
     * @param t         an extracted tree
     * @param migration true if this tree is extracted from a migration animation
     */
    public AnimationTree(ScannerTree t, boolean migration)
    {
	VectorIterator vi = new VectorIterator(t.getNodeList());
	int i = 0;
	int color;

	initAll();
	position = t.getPos();
	isScanner = true;

	while (vi.hasNext()) {
	    ScannerTreeNode s = (ScannerTreeNode)(vi.next());
	    
	    if (migration) {
		if (s.time > 0.0)
		    color = 1;
		else
		    color = (s.ID < (t.leaves/2))?0:3;
	    } else
		color = s.time==0.0?0:1;	    

	    nodes.addElement(new AnimationTreeNode(s.ID,
						   color,
						   s.time,
						   "Time: " + s.time));
	    if (s.left != null) {
		edges.addElement(new AnimationTreeEdge(i++,s.left.ID,s.ID));
		edges.addElement(new AnimationTreeEdge(i++,s.right.ID,s.ID));
	    }
	}
	buildTree();
    }



    /**
     * Return the pool of scannertrees
     */
    public ScannerTrees getScannerTrees() { return strees; }



    /** 

	Input parsing

    */

    /**
     * Parse serialized tree line
     *
     * @param s the serialized line
     */
    private void parseLine(String s) throws TreeFormatException
    {
	char c = s.charAt(0);

	switch (c) {
	case 'N':
	    parseNode(s.substring(2));
	    break;
	case 'E':
	    parseEdge(s.substring(2));
	    break;
	case 'I':
	    c = s.charAt(1);
	    switch (c) {
	    case 'R':
		parseIntervalRange(s.substring(3));
		break;
	    case 'N':
		parseNodeInterval(s.substring(3));
		break;
	    case 'E':
		parseEdgeInterval(s.substring(3));
		break;
	    default:
		throw new TreeFormatException("Bad tree format : interval line starting with " + c);
	    }
	    break;
	default:
	    throw new TreeFormatException("Bad tree format : line starting with " + c);
	}
    }

    /**
     * Tokenize a string
     *
     * @param s     the string to tokenize
     * @param delim token delimiter
     * @param num   expected number of tokens
     */
    private String[] getTokens(String s, String delim, int num) throws TreeFormatException
    {
	String ret[] = new String[num];
	StringTokenizer st = new StringTokenizer(s,delim,false);
	int i;

	i = 0;
	while (st.hasMoreTokens()) {
	    if (i == num) 
		throw new TreeFormatException("Bad tree format : trying to get " + num + " tokens from " + s + "with delim" + delim);
	    ret[i++] = st.nextToken();	    
	}
	return ret;
    }

    /**
     * Parse serialized tree node line
     *
     * @param s the serialized line
     */
    private void parseNode(String s) throws TreeFormatException
    {
	String arg[] = getTokens(s,"|",4);
	
	try {
	    AnimationTreeNode atn = new AnimationTreeNode(Integer.parseInt(arg[0]),
							  Integer.parseInt(arg[1]),
							  (new Double(arg[2])).doubleValue(),
							  arg[3]);
	    height_scale = Math.max(height_scale,atn.time);
	    nodes.addElement(atn);
	} catch (Exception e) {
	    throw new TreeFormatException("Bad tree format : parsing node gave \"" + e + "\"");
	}
    }

    /**
     * Parse serialized edge line
     *
     * @param s the serialized line
     */
    private void parseEdge(String s) throws TreeFormatException
    {
	String arg[] = getTokens(s,"|",3);
	int id,src,dest;

	try {
	    id = Integer.parseInt(arg[0]);
	    src = Integer.parseInt(arg[1]);
	    dest = Integer.parseInt(arg[2]);
	} catch (Exception e) {
	    throw new TreeFormatException("Bad tree format : parsing node gave \"" + e + "\"");
	}

	edges.addElement(new AnimationTreeEdge(id,src,dest));
    }

    /**
     * Parse serialized node interval
     *
     * @param s the serialized line
     */
    private void parseNodeInterval(String s) throws TreeFormatException
    {
	String arg[] = getTokens(s,"|",4);
	AnimationTreeNode a = null;

	//System.err.println(s);

	try {
	    a = getNode(Integer.parseInt(arg[0]));
	} catch (Exception e) {
	    throw new TreeFormatException("Bad tree format : parsing node interval gave \"" + e + "\"");
	}

	if (a == null)
	    throw new TreeFormatException("Bad tree format : cannot attach interval to non-existing node");

	IntervalList inv = new IntervalList(arg[1]);
	IntervalList ans = new IntervalList(arg[2]);
	IntervalList grey = new IntervalList(arg[3]);

	a.setIntervals(inv);
	a.setAncestorIntervals(ans);
	a.setGreyIntervals(grey);
    }


    /**
     * Parse serialized edge interval
     *
     * @param s the serialized line
     */
    private void parseEdgeInterval(String s) throws TreeFormatException
    {
	String arg[] = getTokens(s,"|",3);
	int id;

	try {
	    id = Integer.parseInt(arg[0]);
	} catch (Exception e) {
	    throw new TreeFormatException("Bad tree format : parsing node gave \"" + e + "\"");
	}

	AnimationTreeEdge ate = getEdge(id);
	if (ate == null) 
	    throw new TreeFormatException("Bad tree format : edge does not exist");
		
	IntervalList inv = new IntervalList(arg[1]);
	IntervalList grey = new IntervalList(arg[2]);
	ate.setIntervals(inv);
	ate.setGreyIntervals(grey);
    }

    /**
     * Parse an interval range
     *
     * @param s the serialized string containing the range
     */
    private void parseIntervalRange(String s) throws TreeFormatException
    {
	String arg[] = getTokens(s,"|",1);

	IntervalList inv = new IntervalList(arg[0]);
	if (inv.size() != 1)
	    throw new TreeFormatException("Interval range is invalid");
	
	range = (Interval)inv.elementAt(0);
    }

    /**
     * Read list of edges containing a mutation in the selection tree.
     *
     * @param s    the serialized list
     * @param red  true if the edges should be red (they are green otherwise)
     */
    private void parseSelectionMutations(String s, boolean red) throws TreeFormatException
    {
	StringTokenizer st = new StringTokenizer(s," ",false);
	int id;
	AnimationTreeEdge e;

	while (st.hasMoreTokens()) {
	    s = st.nextToken();
	    try {
		id = Integer.parseInt(s);
	    } catch (Exception ex) {
		throw new TreeFormatException("Mutation id not int");
	    }

	    try {
		e = getEdge(id);
	    } catch (Exception ex) {
		throw new TreeFormatException("Mutation edge does not exist");
	    }

	    if (red)
		e.hasMutation = true;
	    else
		e.hasSelection = true;
	}
    }

    
    /**
     * Read list of nodes with a given type (1 - 'A', 0 - 'a')
     *
     * @param s    the serialized string
     * @param type the type of the nodes in the list
     */
    private void parseSelectionNodeType(String s, char type) throws TreeFormatException
    {
	StringTokenizer st = new StringTokenizer(s," ",false);
	int id;
	AnimationTreeNode n;

	while (st.hasMoreTokens()) {
	    s = st.nextToken();
	    try {
		id = Integer.parseInt(s);
	    } catch (Exception e) {
		throw new TreeFormatException("Selection node id not int");
	    }

	    try {
		n = getNode(id);
	    } catch (Exception e) {
		throw new TreeFormatException("Selection node does not exist");
	    }

	    n.selection_type = type;
	}
    }


    /**
     * Read legend information
     *
     * @param s   the serialized string
     * @param l   the legend listener to feed the legend information
     */
    private void parseLegend(String s, LegendListener l) throws TreeFormatException
    {
	if (l==null) return;

	String arg[] = new String[2];
	int i,id;
	
	for (i=0; i<s.length(); i++)
	    if (s.charAt(i)==' ') break;
	if (i==s.length()) throw new TreeFormatException("Bad legend specification - no space");

	arg[0] = s.substring(0,i);
	arg[1] = s.substring(i+1,s.length());

	id = 0;

	try {
	    id = Integer.parseInt(arg[0]);
	} catch (Exception e) {
	    throw new TreeFormatException("Bad legend specification - first token not int");
	}

	l.addLegend(id,arg[1]);
    }


    /**

       Misc methods

    */
    
    /**
     * Number of nodes in entire tree
     *
     * @return number of nodes in the tree
     */
    public int nodeCount() { return nodes.size(); }


    /**
     * Set the height scale ratio for this tree (used for extracted trees)
     *
     * @param hs  the fixed height scale
     */
    public void setHeightScale(double hs) { height_scale = Math.max(height_scale,hs); }


    /**
     * Returns the position of an extracted tree
     *
     * @return the position of this tree
     */
    public double getPosition() { return position; }


    /**
     * Merge node and edge list to form a tree
     */
    protected void buildTree() {
	VectorIterator vi = new VectorIterator(nodes);
	AnimationTreeNode e;
	AnimationTreeEdge s;
	Vector siblings;

	while (vi.hasNext()) {
	    e = (AnimationTreeNode)(vi.next());
	    
	    VectorIterator si = new VectorIterator(edges);
	    while (si.hasNext()) {
		s = (AnimationTreeEdge)(si.next());

		if (s.getSrcID() == e.ID) { 
		    s.src = e; 
		    e.addOut(s);
		}

		if (s.getDestID() == e.ID) {
		    s.dest = e;
		    e.addIn(s);
		}

	    }
	}
    }


    /**
     * Return nodes in increasing order with respect to ID
     *
     * @return next node with respect to ID
     */
    public AnimationTreeNode nextNode()
    {
	if (nextNode < nodes.size())
	    return (AnimationTreeNode)(nodes.elementAt(nextNode++));
	return null;
    }

    /**
     * Rewind iterator
     */
    public void rewind()
    {
	nextNode = 0;
    }



    /**
      Query methods
    */
    
    /**
     * Get a specific node
     *
     * @param node ID of desired node
     * @return node with desired ID or null if it does not exist
     */
    public AnimationTreeNode getNode(int node)
    {
	VectorIterator vi = new VectorIterator(nodes);
	AnimationTreeNode e;

	while (vi.hasNext()) {
	    e = (AnimationTreeNode)(vi.next());
	    if (e.ID == node) return e;
	}

	return null;
    }    

    /**
     * Get a specific edge
     *
     * @param id ID of edge
     * @return edge with indicated source and destination
     */
    public AnimationTreeEdge getEdge(int id)
    {
	VectorIterator vi = new VectorIterator(edges);
	AnimationTreeEdge ate;

	while (vi.hasNext()) {
	    ate = (AnimationTreeEdge)(vi.next());
	    if (ate.getID() == id) return ate;
	}

	return null;
    }


    /**
     * Get range of intervals in this tree
     *
     * @return the range of intervals
     */
    public Interval getIntervalRange()
    {
	return range;
    }

    /**
     * Get a specific node.
     *
     * @param node ID of desired node
     * @return node with desired ID or the node with the smallest ID
     *         among the nodes with ID larger that the desired ID if no
     *         node with the desired ID exist. Returns null if all nodes
     *         have smaller ID that desired ID.
     */
    public AnimationTreeNode getNodeLargerOrEqualTo(int node)
    {
	VectorIterator vi = new VectorIterator(nodes);
	AnimationTreeNode e, res;
	int id, min;

	min = Integer.MAX_VALUE;
	res = null;
	while (vi.hasNext()) {
	    e = (AnimationTreeNode)(vi.next());
	    id = e.ID;

	    if (id == node) return e;
	    if (id > node && id < min) {
		min = id;
		res = e;
	    }
	}

	return res;
    }

    /**
     * Get node close to a specific point
     *
     * @param x x coordinate of specific point
     * @param y y coordinate of specific point
     */
    public AnimationTreeNode getVisibleNodeCloseTo(int x, int y)
    {
	VectorIterator vi = new VectorIterator(nodes);
	AnimationTreeNode e;
	int nx,ny,count;

	//x -= 5;
	//y -= 5;
	count = nextNode;
	while (vi.hasNext()) {
	    e = (AnimationTreeNode)(vi.next());	   
	    if (Math.abs(x-e.x) < 8 && Math.abs(y-e.y) < 8) return e;
	    if (--count == 0) return null;
	}
	return null;
    }

    /**
     * Get nodes in the neighbourhood of a node
     *
     * @param n the node to get neighbourghhood of
     * @return the visible neighbouring nodes of n
     */
    public Vector getNeighbours(AnimationTreeNode n)
    {
	Vector res = new Vector();
	AnimationTreeEdge e;
	VectorIterator vi;
	double current_time;

	vi = new VectorIterator(n.getIns());
	while (vi.hasNext()) 
	    res.addElement(((AnimationTreeEdge)vi.next()));

	vi = new VectorIterator(n.getOuts());
	if (nextNode < nodes.size()) {
	    current_time = ((AnimationTreeNode)nodes.elementAt(nextNode)).time;
	    while (vi.hasNext()) {
		e = (AnimationTreeEdge)(vi.next());
		if (e.dest.time < current_time)
		    res.addElement(e);
	    }
	} else {
	    while (vi.hasNext()) {
		e = (AnimationTreeEdge)(vi.next());
		res.addElement(e);
	    }
	}

	return res;
    }

    /**
     * Is this tree a selection tree?
     *
     * @return true iff this tree is a selection tree
     */
    public boolean isSelection() { return selection; }

    /**
     * Get the current "magic" selection step
     *
     * @return the current step
     */
    public int getSelectionStep() { return selection_step; }

    /**
     * Set the current "magic" selection step
     *
     * @return the current step
     */
    public void setSelectionStep(int i) { selection_step = i; }



    /** 
	
	Graphical functions follow

    */

    /**
     * Count number of nodes with depth 0.0
     *
     * @return the number of nodes with depth 0.0
     */
    private int initialNodes()
    {
	VectorIterator vi = new VectorIterator(nodes);
	AnimationTreeNode e;
	int count;

	count = 0;
	while (vi.hasNext()) {
	    e = (AnimationTreeNode)(vi.next());
	    if (e.time == 0.0) count++;
	}

	return count;
    }

    /**
     * Find depth of node with largest depth
     *
     * @return depth of node with largest depth
     */
    private double maxTime()
    {
	VectorIterator vi = new VectorIterator(nodes);
	AnimationTreeNode e;
	double max;

	max = 0.0;
	while (vi.hasNext()) {
	    e = (AnimationTreeNode)(vi.next());
	    max = Math.max(e.time,max);
	}

	return max;
    }

    /**
     * Set coordinates of nodes with depth 0.0
     *
     * @param space spacing between the initial nodes
     */
    private void setInitialCoords(double space)
    {
	VectorIterator vi = new VectorIterator(nodes);
	AnimationTreeNode e;
	double offset;
	boolean first_second;

	first_second = true;
	offset = margin;
	while (vi.hasNext()) {
	    e = (AnimationTreeNode)(vi.next());
	    if (e.time == 0.0) {
		e.x = (int)offset;
		e.y = margin;
		if (e.color>0 && first_second) {
		    migration_boundary = (int)(offset-(space/2.0));
		    first_second = false;
		}
		offset += space;
	    }
	}
    }

    /**
     * Find mean x-coordinate in vector of siblings
     *
     * @param siblings integer vector of siblings
     * @return mean x-coordinate in vector of siblings
     */
    private int meanSiblings(Vector siblings, int width)
    {
	VectorIterator vi = new VectorIterator(siblings);
	AnimationTreeNode e;
	int min,max,x;

	min = Integer.MAX_VALUE;
	max = 0;
	while (vi.hasNext()) {
	    e = ((AnimationTreeEdge)(vi.next())).src;

	    min = Math.min(e.x,min);
	    max = Math.max(e.x,max);
	}

	if (isScanner) {
	    x = min + (max - min)/2;
	} else {
	    x = min + (int)(rnd.nextFloat()*(double)(max-min));
	    if (siblings.size()==1 ||
		(siblings.size()==2 &&
		 ((AnimationTreeEdge)(siblings.elementAt(0))).src==
		 ((AnimationTreeEdge)(siblings.elementAt(1))).src))

		x = x + (15-(int)(rnd.nextFloat()*30.0));
	}

	if (x<margin) x = margin + (int)(rnd.nextFloat()*30.0);
	if (x>(width-margin)) x = (width-margin) - (int)(rnd.nextFloat()*30.0);;
	return x;
    }

    /**
     * Set coordinates of nodes with depth > 0.0
     *
     * @param ratio spacing ratio in y direction
     */
    private void setTreeCoords(double ratio, int width)
    {
	VectorIterator vi = new VectorIterator(nodes);	
	AnimationTreeNode e,s;
	Vector siblings,parents;
	int offset;

	while (vi.hasNext()) {
	    e = (AnimationTreeNode)(vi.next());
	    if (e.time != 0.0) {
		e.y = margin + (int)(e.time*ratio);
		siblings = e.getIns();
		parents = e.getOuts();

		if (siblings.size() == 0) {
		    System.out.println("Too few siblings to node " + e.ID);
		    System.exit(1);
		}
		
		if (migration && siblings.size()==1 && parents.size()==1) {
		    AnimationTreeNode son = ((AnimationTreeEdge)(siblings.elementAt(0))).src;
		    e.migration = true;
		    if (son.x < migration_boundary)
			e.x = migration_boundary+10+(int)(rnd.nextFloat()*50);
		    else
			e.x = migration_boundary-10-(int)(rnd.nextFloat()*50);
		} else {
		    e.x = meanSiblings(siblings,width);
		}
	    }
	}
    }


    /**
     * Set coordinates of edge pairs that share source
     * and destination (eg. coalescent on both outgoing
     * edges of a recombination)
     */
    private void setRecombinationCoords()
    {
	VectorIterator vi = new VectorIterator(nodes);
	AnimationTreeNode n,f;
	int mx,my;
	double alpha,k,dx;

	while (vi.hasNext()) {
	    n = (AnimationTreeNode)(vi.next());
	    if (n.outs.size() == 2 &&
		((AnimationTreeEdge)(n.outs.elementAt(0))).dest ==
		((AnimationTreeEdge)(n.outs.elementAt(1))).dest) {
		AnimationTreeEdge e1 = (AnimationTreeEdge)(n.outs.elementAt(0));
		AnimationTreeEdge e2 = (AnimationTreeEdge)(n.outs.elementAt(1));	       
		f = ((AnimationTreeEdge)(n.outs.elementAt(1))).dest;

		e1.has_coord = true;
		e2.has_coord = true;

		// Switch
		while (true) {
		    // Case nodes are vertically aligned
		    if (f.x == n.x) {
			e1.x = n.x-4;
			e1.y = n.y + (f.y-n.y)/2;
			e2.x = n.x+4;
			e2.y = e1.y;
			break;
		    }
		    
		    // Case nodes are horizontally aligned
		    if (f.y == n.y) {
			e1.x = n.x + (f.x-n.x)/2;
			e1.y = n.y-4;
			e2.x = e1.x;
			e2.y = n.y+4;
			break;
		    }

		    // Default
		    alpha = -1.0/( (double)(f.y-n.y)/(double)(f.x-n.x) );
		    mx = n.x + (f.x-n.x)/2;
		    my = n.y + (f.y-n.y)/2;
		    k = my - alpha*mx;
		    dx = Math.sqrt(25.0/(1+alpha*alpha));

		    e1.x = (int)(mx+dx);
		    e1.y = (int)(alpha*e1.x + k);
		    
		    e2.x = (int)(mx-dx);
		    e2.y = (int)(alpha*e2.x + k);
		    break;
		}
	    }
	}
    }

    /**
     * Transform y-coordinates from normal coordinate system
     * coordinates to "upside-down" coordinates.
     *
     * @param height height of display
     */
    protected void invertYCoords(int height)
    {
	VectorIterator vi = new VectorIterator(nodes);	
	AnimationTreeNode n;
	AnimationTreeEdge e;

	while (vi.hasNext()) {
	    n = (AnimationTreeNode)(vi.next());
	    n.y = (height - n.y);
	}

	vi = new VectorIterator(edges);
	while (vi.hasNext()) {
	    e = (AnimationTreeEdge)(vi.next());
	    if (e.has_coord)
		e.y = (height - e.y);
	}

    }

    /**
     * The x-coordinate of the migration boundary
     *
     * @return the x-coordinate of the migration boundary (-1 if not a migration tree)
     */
    public int migrationBoundary()
    {
	if (!migration) return -1;
	return migration_boundary;
    }

    /**
     * Layout nodes according to the size of a specific display
     *
     * @param width width of specific display
     * @param height height of specific display
     * @return number of nodes with depth 0.0
     */
    public int calculateCoordinates(int width, int height)
    {
	int num_nodes;
	double node_space, height_ratio;


	num_nodes = initialNodes();
	
	node_space = ((double)width - 2.0*(double)margin)/((double)num_nodes - 1.0);
	setInitialCoords(node_space);

	height_ratio = ((double)height - 2.0*(double)margin)/height_scale;
	setTreeCoords(height_ratio,width);
	setRecombinationCoords();

	invertYCoords(height);

	return num_nodes;
    }
}
