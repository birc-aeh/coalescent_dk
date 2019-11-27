
import java.awt.*;

/** 
 * Class for scaling and iterating through the nodes/generations in
 * a set of generations
 * 
 * @author Anders Mikkelsen
 * @see Generation
 * @see Generations
 */
public class GenerationsAnimator
{
    /** 
     * The set of generations
     */
    private Generations g;

    /** 
     * For iterating - the index of the current generation
     */
    private int step;

    /** 
     * Make a new animator/iterator from a set of generations
     * 
     * @param g the generations to iterate over
     */
    public GenerationsAnimator(Generations g)
    {
	this.g = g;
	step = 0;
    }

    /** 
     * Get the set of generations associated with this iterator
     * 
     * @return the set of generations associated with this iterator
     */
    public Generations generations() { return g; }

    /** 
     * Iterating - Rewind this animator to point to the first generation
     */
    public void rewind() { step = 0; }

    /** 
     * Iterating - get the next generation and update step counter
     * 
     * @return the next generation
     */
    public Generation nextGeneration()
    {
	if (step >= g.layers()) return null;

	return g.generation(step++);
    }

    /** 
     * Get the first generation in the set of generations
     * 
     * @return the first generation in the set of generations associated with 
     *     this iterator
     */
    public Generation firstGeneration() { return g.generation(0); }
    /** 
     * Get the last generation (ie. youngest) in the set of generations
     * associated with this iterator
     * 
     * @return the youngest generation in the set associated with this 
     *     iterator
     */
    public Generation lastGeneration() { return g.generation(g.layers()-1); }

    /** 
     * Iterating - Take a peek at the next generation without updating
     * the step counter
     * 
     * @return the next generation
     */
    public Generation snoopGeneration()
    {
	if (step >= g.layers()) return null;
	return g.generation(step);
    }

    /** 
     * Find the longest possible path from a node downwards in the ancestral
     * hierarchy
     * 
     * @param n source node of the search
     * @return number of nodes on the longest path from this node to a younger
     *      node
     */
    public int longestPathFrom(Node n)
    {
	int max = 0;
	VectorIterator vi = new VectorIterator(n.siblings);
	while (vi.hasNext()) {
	    Node s = (Node)(vi.next());
	    max = Math.max(max,longestPathFrom(s));
	}
	return max+1;
    }

    /** 
     * Assign pixel-wise coordinates to all nodes in the set associated with
     * this iterator. Also colors (path and node) are assigned by this method.
     * 
     * @param width the width of the display (pixels)
     * @param height the height of the display (pixels)
     */
    public void scale(int width, int height)
    {
	double x,y,xratio,yratio;
	int i,j;

	yratio = (double)(height-20)/(g.layers()-1);
	xratio = (double)(width-20)/(g.genes()-1);
	Colors.rewind();

	y = 10.0;
	for (i=0; i<g.layers(); i++) {
	    Generation gen = g.generation(i);
	    x = 10.0;
	    for (j=0; j<g.genes(); j++) {
		Node n = gen.node(j);
		n.x = (int)x;
		n.y = (int)y;
		x += xratio;
		n.clickable = (i==0 || i==g.layers()-1);
		n.path_color = GUIConfig.stdpathcolor;
		n.ancestor = (i==0 && longestPathFrom(n)==g.layers());
		if (n.ancestor) n.path_color = Colors.next();
	    }
	    y += yratio;
	}

	Generation gen = g.generation(0);
	for (j=0; j<g.genes(); j++) {
	    Node n = gen.node(j);
	    if (n.ancestor)
		setPathColor(n,n.path_color);
	}
    }

    /** 
     * Recolor a path (actually an entire tree) downwards in the ancestry
     * 
     * @param n source node of the recoloring
     * @param c the new path (tree) color
     */
    private void setPathColor(Node n, Color c)
    {
	VectorIterator vi = new VectorIterator(n.siblings);
	while (vi.hasNext()) {
	    Node s = (Node)(vi.next());
	    s.path_color = c;
	    setPathColor(s,c);
	}
    }


    /** 
     * Check if a node in a given generation is close to a given
     * coordinate (pixel-wise)
     * 
     * @param gen the generation to scan
     * @param x x-coordinate
     * @param y y-coordinate
     * @return the node close to the coordinate (null if node nodes are close)
     */
    private Node getNodeInGeneration(Generation gen, int x, int y)
    {
	VectorIterator vi = new VectorIterator(gen);
	while (vi.hasNext()) {
	    Node n = (Node)(vi.next());
	    if (Math.abs(n.x-x)<7 && Math.abs(n.y-y)<7)
		return n;
	}
	return null;
    }

    /** 
     * Check if any nodes in the youngest generation (ie. the bottom
     * generation) are close to a given coordinate.
     * 
     * @param x x-coordinate
     * @param y y-coordinate
     * @return a node in the youngest generation close to the coordinate (null
     *      if none are close)
     */
    public Node getNodeCloseToBottom(int x, int y)
    {
	Generation gen = g.generation(g.layers()-1);
	return getNodeInGeneration(gen,x,y);
    }


    /** 
     * Check if any nodes in the oldest generation (ie. the top generation)
     * are close to a given coordinate
     * 
     * @param x x-coordinate
     * @param y 
     * @return a node in the oldest generation close to the coordinate (null  
     *     if none are close)
     */
    public Node getNodeCloseToTop(int x, int y)
    {
	Generation gen = g.generation(0);
	return getNodeInGeneration(gen,x,y);	
    }



}
