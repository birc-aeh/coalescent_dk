
import java.awt.*;
import java.awt.event.*;

/** 
 * The main animation canvas
 * 
 * @author Anders Mikkelsen
 */
public class Evolver extends BufferedCanvas implements ModelListener, ImpulseListener
{
    /** 
     * Animator for the nodes in this canvas
     * 
     */
    GenerationsAnimator manim;

    /** 
     * Animation timer
     * 
     */
    ImpulseGenerator timer;

    /** 
     * Current delay between animation updates (msec)
     * 
     */
    int timer_delay;

    /** 
     * Control panel listening on this animation
     * 
     */
    ControlListener cListener;

    /** 
     * Are we currently drawing from bottom to top?
     * 
     */
    boolean up_color;

    /** 
     * Currently unused
     * 
     */
    private Color hilitcolor = Color.red;

    /** 
     * Std. node color
     * 
     */
    private Color normalcolor = Color.gray;

    /** 
     * Std. line color
     * 
     */
    private Color linecolor = Color.gray;

    /** 
     * Currently unused
     * 
     */
    private Color ancestorcolor = Color.green;

    /** 
     * Create a new main animation canvas
     * 
     * @param width desired width of the canvas
     * @param height desired height of the canvas
     */
    public Evolver(int width, int height)
    {
	super(width,height);
	setBackground(Color.white);

	timer = null;
	modelSpeed(50);
	addMouseListener(new smallMouseListener());
	addMouseMotionListener(new smallMouseMotionListener());
	up_color = true;
	
    }
    
    /** 
     * Change the current drawing direction (bottom to top/top to bottom).
     * Clears the currently highlighted path and hilights a new in the
     * other direction
     * 
     * @param up true if the new direction should be bottom to top
     * @param node node to draw the new path from
     */
    private void changeColorDirection(boolean up, Node node) {
	Generation gen;

	if (up != up_color) {
	    up_color = up;

	    if (up)
		gen = manim.firstGeneration();
	    else
		gen = manim.lastGeneration();

	    hilitcolor = Color.magenta;
	    VectorIterator vi = new VectorIterator(gen);
	    while (vi.hasNext()) {
		Node n = (Node)(vi.next());
		if (n.paths_passing > 0) {
		    if (up)
			renderDownPaths(n);
		    else
			renderPath(n);
		}
	    }

	    if (up)
		hilitcolor = Color.red;
	    else
		hilitcolor = Color.blue;
	}
    }

    /** 
     * The currently highlighted node
     * 
     */
    private Node hilited;

    private class smallMouseListener extends MouseAdapter
    {
        /** 
         * React on the user clicking the mouse in the canvas
         * 
         * @param ev mouse event
         */
	public void mouseClicked(MouseEvent ev)
	{
	    if (timer!=null || manim==null || manim.snoopGeneration()!=null) return;

	    Node n = manim.getNodeCloseToBottom(ev.getX(),ev.getY());
	    if (n!=null) {
		unhilitNode();
		changeColorDirection(true,n);
		renderPath(n);	   
		hilitNode(n,Color.red);
		return;
	    }

	    n = manim.getNodeCloseToTop(ev.getX(),ev.getY());
	    if (n!=null) {
		unhilitNode();
		changeColorDirection(false,n);
		renderDownPaths(n);
		hilitNode(n,Color.blue);
		return;
	    }
	}

        /** 
         * React on the mouse leaving the canvas
         * 
         * @param ev 
         */
	public void mouseExited(MouseEvent ev) 
	{
	    unhilitNode();
	}
	
    }

    private class smallMouseMotionListener extends MouseMotionAdapter
    {
        /** 
         * React on the mouse being moved
         * 
         * @param ev 
         */
	public void mouseMoved(MouseEvent ev)
	{
	    if (timer!=null || manim==null || manim.snoopGeneration()!=null) return;
	    Node n = manim.getNodeCloseToBottom(ev.getX(),ev.getY());
	    if (n!=null) {
		hilitNode(n,Color.red);
		return;
	    }
	    n = manim.getNodeCloseToTop(ev.getX(),ev.getY());
	    if (n!=null) {
		hilitNode(n,Color.blue);
		return;
	    }
	    unhilitNode();
	}
    }

    /** 
     * Set a control listener for this evolver
     * 
     * @param c the new control listener
     */
    public void setControlListener(ControlListener c) { cListener = c; }

    /** 
     * Paint a node on this canvas
     * 
     * @param g graphics for this canvas
     * @param n the node to draw
     */
    private void renderNode(Graphics g, Node n)
    {
	if (n.ancestor) {
	    //setRenderColor(g,ancestorcolor);
	    setRenderColor(g,n.path_color);
	}
	renderFilledCircle(g,n.x,n.y,n.diameter);

	setRenderColor(g,Color.black);
	renderCircle(g,n.x,n.y,n.diameter);
	if (n.clickable)
	    renderCircle(g,n.x,n.y,n.diameter+4);
    }

    /** 
     * Un-highlight the currently highlighted node
     * 
     */
    private void unhilitNode()
    {
	if (hilited==null) return;
	Graphics g = getGraphics();
	setRenderColor(g,Color.black);
	renderCircle(g,hilited.x,hilited.y,hilited.diameter+4);
	hilited = null;
    }

    /** 
     * Highlight a node
     * 
     * @param n the node to highlight
     * @param c the color to highlight the node with
     */
    private void hilitNode(Node n, Color c)
    {
	Graphics g = getGraphics();
	if (hilited == n) return;
	unhilitNode();
	setRenderColor(g,c);
	renderCircle(g,n.x,n.y,n.diameter+4);
	hilited = n;
    }

    /** 
     * Draw a node, ie. paint the node and surrounding edges and nodes
     * 
     * @param g gra[hics for this canvas
     * @param n the node to draw
     */
    private void drawNode(Graphics g, Node n)
    {

	Node p = n.parent;

	if (p!=null) {
	    if (n.paths_passing>0) {
		//setRenderColor(g,hilitcolor);
		setRenderColor(g,n.path_color);
		renderLine(g,n.x-1,n.y,p.x-1,p.y);
	    }

//  	    setRenderColor(g,(n.paths_passing>0)?hilitcolor:linecolor);
  	    setRenderColor(g,(n.paths_passing>0)?n.path_color:linecolor);
	    renderLine(g,n.x,n.y,p.x,p.y);
//	    setRenderColor(g,(p.paths_passing>0)?hilitcolor:normalcolor);
	    setRenderColor(g,(p.paths_passing>0)?p.path_color:normalcolor);
  	    renderNode(g,p);
	}
//  	setRenderColor(g,(n.paths_passing>0)?hilitcolor:normalcolor);
	setRenderColor(g,(n.paths_passing>0)?n.path_color:normalcolor);
	renderNode(g,n);
    }


    /** 
     * Render a path, bottom to top
     * 
     * @param n the node in which to start rendering
     */
    private void renderPath(Node n)
    {
	Graphics g = getGraphics();
	boolean painting;
	Color nc,lc;

	painting = (n.paths_passing==0);
	while (n != null) {
	    if (painting)
		n.paths_passing++;
	    else {
		n.paths_passing--;
	    }
	   
//  	    nc = (n.paths_passing > 0)?hilitcolor:normalcolor;
//  	    lc = (n.paths_passing > 0)?hilitcolor:linecolor;
	    nc = (n.paths_passing > 0)?n.path_color:normalcolor;
	    lc = (n.paths_passing > 0)?n.path_color:linecolor;
	    
	    if (n.parent == null) {
		setRenderColor(g,nc);
		renderNode(g,n);
		break;
	    }

	    // Double line width
	    //setRenderColor(g,(n.paths_passing>0)?hilitcolor:Color.white);
	    setRenderColor(g,(n.paths_passing>0)?n.path_color:Color.white);
	    renderLine(g,n.x-1,n.y,n.parent.x-1,n.parent.y);	

	    setRenderColor(g,lc);
	    renderLine(g,n.x,n.y,n.parent.x,n.parent.y);

	    setRenderColor(g,nc);
	    renderNode(g,n);
	    n = n.parent;
	}
    }

    /** 
     * Render a tree - top to bottom
     * 
     * @param n the node in which to start rendering
     */
    private void renderDownPaths(Node n)
    {
	if (n.siblings.size()==0) {
	    renderPath(n);
	    return;
	}

	VectorIterator vi = new VectorIterator(n.siblings);
	while (vi.hasNext()) {
	    Node s = (Node)(vi.next());
	    renderDownPaths(s);
	}
    }


    /** 
     * Step animation one step forward
     * 
     * @return true on success, false otherwise
     */
    public boolean modelStep()
    {
	int i;
	boolean last;

	if (manim==null) return false;

	Graphics grp = getGraphics();
	Generation g = manim.nextGeneration();
	if (g==null) {
	    modelStop();
	    return false;
	}

	last = (manim.snoopGeneration() == null);
	VectorIterator vi = new VectorIterator(g);
	while (vi.hasNext()) {
	    drawNode(grp,(Node)(vi.next()));
	}

	return true;
    }

    /** 
     * Stop current animation
     * 
     */
    public void modelStop()
    {
	if (timer != null) {
	    timer.halt();
	    timer = null;
	}
    }

    /** 
     * Start current animation
     * 
     */
    public void modelStart()
    {
	if (timer != null)
	    modelStop();

	timer = new ImpulseGenerator(this,timer_delay);
	timer.start();	
    }

    /** 
     * Make a new animation
     * 
     * @param m the model for the new animation
     */
    public void modelNew(Model m)
    {
	modelStop();
	manim = new GenerationsAnimator(m.generations());
	manim.scale(width,height);
	clearToBack();
	modelStep();
	//modelStart();
	StatusBar.setStatus("Ready");

	if (cListener != null)
	    cListener.setButtonState(true);
    }

    /** 
     * Flush animation
     * 
     */
    public void modelFlush()
    {
	while (modelStep());
    }

    /** 
     * Untangle the current animation, ie. make it readable
     * 
     */
    public void modelUntangle()
    {
	if (manim==null) return;

	modelStop();
	manim.rewind();
	manim.generations().unTangle();
	manim.scale(width,height);
	clearToBack();
	modelFlush();
    }

    /** 
     * Change animation speed
     * 
     * @param s new animation speed (in percent)
     */
    public void modelSpeed(int s)
    {
        if (s==0) s++;

        timer_delay = (int)(-2249.49 - 12494.97/s + 24744.47/(Math.sqrt(s)));

        if (timer != null)
            timer.setTime(timer_delay);       

	//System.out.println(timer_delay);
    }

    /** 
     * Rewind the current animation
     * 
     */
    public void modelRewind()
    {
	if (manim==null) return;
	modelStop();
	manim.rewind();
	clearToBack();
	modelStep();
    }



    /** 
     * React on impulse from the timer
     * 
     */
    public boolean consumeImpulse()
    {
	return modelStep();
    }



}
