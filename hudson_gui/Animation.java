
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * The Animation class provides an interface for making generic
 * tree animations. It operates on AnimationTrees
 *
 * @author Anders Mikkelsen
 * @see AnimationTree
 */
public class Animation extends BufferedCanvas implements AnimationListener
{
    /// Timer updating the animation
    AnimationTimer timer = null;

    /// Timer delay
    int timer_delay = 1000;

    /// An infolistener
    InfoListener iListener;

    /// A control listener
    ControlListener cListener;

    /// Font for rendering the "magic" selection buttons
    Font f;

    /// Font height
    int font_height;

    /// With of widest text on the magic button
    int magic_button_text_width;

    /// Magic button texts
    String magic_buttons[] = { "", "Show mutations", "Show types", "Show nothing", "Four" };


    /**
     * Make an animation window with indicated width and height
     *
     * @param width  desired width
     * @param height desired height
     */
    public Animation(int width, int height)
    {	
	super(width,height);

	setBackground(Color.white);
	addMouseListener(new smallMouseListener());
	addMouseMotionListener(new smallMouseMotionListener());
	initColors();
    }

    /**
     * Set an info listener
     *
     * @param i the infolistener
     */
    public void setInfoListener(InfoListener i)
    {
	iListener = i;
    }

    /**
     * Set a control listener
     *
     * @param c the control listener
     */
    public void setControlListener(ControlListener c)
    {
	cListener = c;
    }

    /**
     * The current animation tree
     */
    AnimationTree current_tree;

    /**
     * Number of base nodes in current tree, ie. the number of nodes
     * that must be initially drawn (on a rewind)
     */
    int current_basesize;

    /**
     * Number of node colors available
     */
    int num_colors;

    /**
     * Array of colors available
     */
    Color color[];

    /**
     * The currently highlighted node (or null if none)
     */
    private AnimationTreeNode last_hilited_node = null;

    /**
     * A small class listening for mouse clicks in the display
     */
    private class smallMouseListener extends MouseAdapter
    {
	/**
	 * Called when the mouse is clicked
	 *
	 * @param e information on the mouse
	 */
	public void mouseExited(MouseEvent e)
	{
	    if (last_hilited_node!=null) {
		unHilitNode(last_hilited_node);
		last_hilited_node = null;
	    }
	}

	/**
	 * Called on mouse clicks
	 *
	 * @param e information about the mouse
	 */
	public void mouseClicked(MouseEvent e)
	{
	    int x,y;

	    if (current_tree==null || !current_tree.isSelection() ||
		current_tree.getSelectionStep()==0) return;

	    x = e.getX();
	    y = e.getY();

	    if (x>(width-magic_button_text_width-15) && x<(width-5) &&
		y>4 && y<(font_height+10)) {
		int i = current_tree.getSelectionStep();
		if (i>0 && i<4) {		
		    i = 1 + i%3;
		    current_tree.setSelectionStep(i);
		    animationRedraw();
		    drawMagicButton(i);
		}
	    }
	}

    }


    /**
     * A small class listening for mouse motion
     */
    private class smallMouseMotionListener extends MouseMotionAdapter
    {

	/**
	 * Called on mouse motion. Hilit nearby node and siblings.
	 */
	public void mouseMoved(MouseEvent e)
	{
	    if (current_tree == null) return;

	    AnimationTreeNode n = current_tree.getVisibleNodeCloseTo(e.getX(),e.getY());
	    if (last_hilited_node!=null && last_hilited_node!=n) {
		unHilitNode(last_hilited_node);
		last_hilited_node = null;
	    }
	    if (n!=null && last_hilited_node!=n) {
		hilitNode(n);
		last_hilited_node = n;
		if (iListener!=null)
		    iListener.update(n, current_tree.getIntervalRange(), current_tree.isSelection());
	    }
	}
    }

    /**
     * Gather font information when connected to native system
     */
    public void addNotify()
    {
	int i;
        super.addNotify();

	
	f = new Font("SansSerif", Font.BOLD, 10);
	ig.setFont(f);

	FontMetrics fm = ig.getFontMetrics();
	font_height = fm.getHeight();
	magic_button_text_width = 0;
	for (i=0; i<magic_buttons.length; i++) {
	    int l = fm.stringWidth(magic_buttons[i]);
	    magic_button_text_width = l>magic_button_text_width?l:magic_button_text_width;
	}
    }

    /**
     * Allocate colors
     */
    private void initColors()
    {
	color = new Color[6];
	num_colors = 6;
	
	color[0] = new Color(255,0,0);
	color[1] = new Color(0,255,0);
	color[2] = new Color(0,0,255);
	color[3] = new Color(255,255,0);
	color[4] = new Color(255,0,255);
	color[5] = new Color(0,255,255);
    }

    /**
     * Lookup color in array of allocated colors
     */
    private Color getColor(int i)
    {
	if (i<num_colors)
	    return color[i];
	return Color.black;
    }
    

    /**
     * Draw a "dot", ie. a node in the display and on the buffer image
     *
     * @param dpy       the display
     * @param c         node color
     * @param x         x coordinate of node
     * @param y         y coordinate of node
     * @param diameter  diameter (in pixels) of node
     */
    private void renderDot(Graphics dpy, int x, int y, int diameter)
    {
	Color c;

	c = dpy.getColor();
	renderFilledCircle(dpy,x,y,diameter);
	setRenderColor(dpy,Color.black);
	renderCircle(dpy,x,y,diameter);
	setRenderColor(dpy,c);
    }


    /**
     * Draw a mark, ie. one or two small bars on top a node
     * in a selection tree.
     *
     * @param dpy     the display
     * @param x       x coordinate of mark
     * @param y       y coordinate of mark
     * @param type    the type (1 for 'A' and 0 for 'a')
     */
    private void renderMark(Graphics dpy, int x, int y, char type)
    {
	if (type==2)
	    setRenderColor(dpy,Color.white);
	else
	    setRenderColor(dpy,Color.black);
	renderFilledRect(dpy,x,y-7,2,7);

	if (type>0) 
	    renderFilledRect(dpy,x+3,y-7,2,7);
    }


    /**
     * Draw a node and refresh siblings to avoid lines on top of nodes
     *
     * @param n the node to be drawn
     */
    private void drawNode(AnimationTreeNode n)
    {
	Graphics g = getGraphics();

	Vector n_hood = current_tree.getNeighbours(n);
	VectorIterator vi = new VectorIterator(n_hood);
	AnimationTreeNode s;
	AnimationTreeEdge e;

	boolean issel = current_tree.isSelection();
	int selst = current_tree.getSelectionStep();
	
	vi.rewind();

	// Erase marks
	if (issel && selst!=3 && n.visible_marks) {
 	    renderMark(g,n.x+2,n.y-2,(char)2);
	    n.visible_marks = false;
	}

	while (vi.hasNext()) {
	    e = (AnimationTreeEdge)(vi.next());
	    if (e.dest == n) s=e.src; else s=e.dest;

	    setRenderColor(g,Color.black);

	    if (issel) {
		if (selst>=2 && e.hasMutation) setRenderColor(g,Color.red);
	    }

	    while (true) {
		// The edge has coordinates
		if (e.has_coord) {
		    renderLine(g,n.x,n.y,e.x,e.y);
		    renderLine(g,e.x,e.y,s.x,s.y);
		    break;
		}
		// n is migration and s is son
		if (n.migration && s.time<n.time) {
		    int offset = Math.abs(n.x-s.x)<10?0:(n.x>s.x?5:-5);
		    renderLine(g,n.x,n.y,s.x+offset,n.y);
		    renderLine(g,s.x+offset,n.y,s.x,s.y);
		    break;
		}
		// s is parent and s is migration
		if (s.migration && s.time>n.time) {
		    int offset = Math.abs(n.x-s.x)<10?0:(n.x>s.x?-5:5);
		    renderLine(g,n.x,n.y,n.x+offset,s.y);
		    renderLine(g,n.x+offset,s.y,s.x,s.y);
		    break;
		}
		renderLine(g,n.x,n.y, s.x,s.y);
		break;
	    }

	    setRenderColor(g,getColor(s.color));
	    renderDot(g,s.x,s.y,s.diameter);

	    // Render marks on selection step 3
	    if (issel && selst==3) {
		renderMark(g,s.x+2,s.y-2,s.selection_type);
		s.visible_marks = true;
	    }
	}
	
	setRenderColor(g,getColor(n.color));
	renderDot(g,n.x,n.y,n.diameter);

	// Render marks on selection step 3
	if (issel && selst==3) {
 	    renderMark(g,n.x+2,n.y-2,n.selection_type);
	    n.visible_marks = true;
	}

    }


    /**
     * Draw the boundary between population when using migration
     */
    private void drawMigrationBoundary()
    {
	Graphics g = getGraphics();
	int i;
	int x = current_tree.migrationBoundary();
	if (x==-1) return;

	setRenderColor(g,Color.gray);
	for (i=4; i<height-4; i+=8)
	    renderLine(g,x,i,x,i+4);
    }


    /**
     * Draw a string of text
     *
     * @param grp    graphics for the window
     * @param s      the string to draw
     * @param x      x-coordinate of text
     * @param y      y-coordinate of text
     */
    private void renderString(Graphics grp, String s, int x, int y)
    {
	grp.drawString(s,x,y);
	ig.drawString(s,x,y);
    }


    /**
     * Draw the "magic" top right selection button
     *
     * @param i    the current selection step
     */    
    private void drawMagicButton(int i)
    {
	int twidth;
	String s = magic_buttons[i];
	Graphics g = getGraphics();
	g.setFont(f);
	
	setRenderColor(g,GUIConfig.buttonbgcolor);
	renderFilledRect(g,width-15-magic_button_text_width,4,magic_button_text_width+10,font_height+6);
	
	twidth = ig.getFontMetrics().stringWidth(s);
	setRenderColor(g,Color.black);

	renderRect(g,width-15-magic_button_text_width,4,magic_button_text_width+10,font_height+6);
	renderString(g,s,width-10-(magic_button_text_width+twidth)/2,font_height+5);
    }



    /**
     * Highlight node and siblings
     */
    private void hilitNode(AnimationTreeNode n)
    {
	Graphics g = getGraphics();
	AnimationTreeNode s;
	Vector v;
	
	VectorIterator vi = new VectorIterator(current_tree.getNeighbours(n));
	setRenderColor(g,Color.blue);
	while (vi.hasNext()) {
	    AnimationTreeEdge e = (AnimationTreeEdge)(vi.next());
	    if (e.dest == n) s=e.src; else s=e.dest;
	    renderCircle(g,s.x,s.y,s.diameter+4);
	}

	renderCircle(g,n.x,n.y,n.diameter+4);
    }

    /**
     * Remove highlight from node
     */
    private void unHilitNode(AnimationTreeNode n)
    {
	Graphics g = getGraphics();
	AnimationTreeNode s;

	VectorIterator vi = new VectorIterator(current_tree.getNeighbours(n));
	while (vi.hasNext()) {
	    AnimationTreeEdge e = (AnimationTreeEdge)(vi.next());
	    if (e.dest == n) s=e.src; else s=e.dest;
	    setRenderColor(g,Color.white);
	    renderCircle(g,s.x,s.y,s.diameter+4);
	    drawNode(s);
	}

	setRenderColor(g,Color.white);
	renderCircle(g,n.x,n.y,n.diameter+4);
	drawNode(n);
    }


    /**
     * Redraw the entire tree
     */
    public void animationRedraw()
    {
	current_tree.rewind();
	AnimationTreeNode n = current_tree.nextNode();
	while (n != null) {
	    drawNode(n);
	    n = current_tree.nextNode();
	}
    }


    /**
     * Prepare for animation - does nothing right now
     */
    public void animationPrepare() { }


    /**
     * Start a new animation
     *
     * @param t the tree to animate
     */
    public void animationNew(AnimationTree t)
    {
	animationStop();
  	current_tree = t;
  	current_basesize = t.calculateCoordinates(width,height);	
  	animationRewind();
  	//animationStart();
	if (cListener != null)
	    cListener.setButtonState(true);
    }

    /**
     * Start the current animation
     */
    public void animationStart()
    { 
	if (current_tree != null && timer == null) {
	    if (current_tree.isSelection() && current_tree.getSelectionStep()>0) return;
	    timer = new AnimationTimer(this,timer_delay);
	    timer.start();
	}
    }

    /**
     * Stop the current animation
     */
    public void animationStop() 
    { 
	//System.out.println("Stop : " + timer);

	if (timer != null) {
	    timer.halt();
	    timer = null;
	}
    }

    /**
     * Rewind the current animation
     */
    public void animationRewind()
    { 
	int i;

	animationStop();
	//nextNode = 0;
	current_tree.rewind();
	clearToBack();
	drawMigrationBoundary();
	current_tree.setSelectionStep(0);
	for (i=0; i<current_basesize; i++)
	    animationStep(true);
    }

    /**
     * Step current animation forward one node
     */
    public boolean animationStep(boolean forward)
    { 
	if (!forward) return true;

	AnimationTreeNode n = current_tree.nextNode();
	if (n == null) {
	    animationStop();
	    if (current_tree.isSelection() && current_tree.getSelectionStep()==0) {
		current_tree.setSelectionStep(1);
		drawMagicButton(1);
	    }
	    return false;
	}

	drawNode(n);
	return true;
    }

    /**
     * Step current animation forward all nodes
     */
    public void animationFlush()
    {
	while (animationStep(true));
    }

    /**
     * Set animation speed
     *
     * @param s speed in range 0 to 100
     */
    public void animationSpeed(int s)
    {
	if (s==0) s++;

	timer_delay = (int)(-2249.49 - 12494.97/s + 24744.47/(Math.sqrt(s)));

	if (timer != null)
	    timer.setTime(timer_delay);       
    }

}
