
import java.awt.*;
import java.awt.event.*;

/**
 * A slider to show the positions at which the extracted tree
 * is altered.
 * 
 * @author Anders Mikkelsen
 */
public class Slider extends BufferedCanvas implements AnimationListener
{
    /// A control listener
    ControlListener cListener;

    /// An animation listener
    AnimationListener aListener;

    /// Offset from top
    static int yoff = 5;

    /// The current extracted tree
    AnimationTree current_tree;

    /// The current offset of the slider
    int slider_offset;

    /// Make sure the slider is drawn initially
    boolean has_drawn = false;
    
    /**
     * New slider
     *
     * @param width desired width
     */
    public Slider(int width)
    {
	super(width,30);
	setBackground(GUIConfig.buttonbgcolor);
	addMouseListener(new smallMouseListener());
    }

    /**
     * Set control listener. It is the slider that listens for animations
     * on the scanner card and is thus responsible for enabling the control
     * buttons on the card.
     *
     * @param c the control listener
     */
    public void setControlListener(ControlListener c) { cListener = c; }

    
    /**
     * The slider controls the displaying of extracted trees on the
     * scanner card through the animation listener interface.
     *
     * @param a the animation listener
     */
    public void setAnimationListener(AnimationListener a) { aListener = a; }


    /**
     * Draw a recombination point on the slider
     *
     * @param dpy graphics for this canvas
     * @param pos position of point (pixels)
     */
    private void drawTag(Graphics dpy, int pos)
    {
	renderLine(dpy,pos,yoff+1,pos,yoff+4);
	renderLine(dpy,pos-1,yoff+1,pos-1,yoff+3);
	renderLine(dpy,pos-2,yoff+1,pos-2,yoff+2);
	renderLine(dpy,pos+1,yoff+1,pos+1,yoff+3);
	renderLine(dpy,pos+2,yoff+1,pos+2,yoff+2);
    }


    /**
     * Draw all recombination points
     *
     * @param dpy graphics for this canvas
     */
    private void drawTags(Graphics dpy)
    {
	if (current_tree == null) return;
	double ratio;

	ratio = current_tree.getIntervalRange().getTo();
	ratio = (width-20)/ratio;

	VectorIterator vi = new VectorIterator(current_tree.getScannerTrees());
	setRenderColor(dpy,Color.blue);
	while (vi.hasNext()) {
	    AnimationTree t = (AnimationTree)(vi.next());
	    if (t.getPosition() == 0.0) continue;
	    int pos = (int)(10+t.getPosition()*ratio);
	    drawTag(dpy,pos);
	}
    }

    /// Position of pointer [0,R/2.0]
    double pointer_value = 0.5;

    /**
     * Draw the pointer
     *
     * @param dpy graphics for this canvas
     */
    private void drawPointer(Graphics dpy)
    {
	if (current_tree == null) return;
	double ratio;

	ratio = current_tree.getIntervalRange().getTo();
	ratio = (width-20)/ratio;
	int pos = (int)(10+pointer_value*ratio);

	renderLine(dpy,pos,yoff+7,pos,yoff+17);
	renderLine(dpy,pos-1,yoff+8,pos-1,yoff+17);
	renderLine(dpy,pos-2,yoff+9,pos-2,yoff+11);
	renderLine(dpy,pos-3,yoff+10,pos-3,yoff+11);

	renderLine(dpy,pos+1,yoff+8,pos+1,yoff+17);
	renderLine(dpy,pos+2,yoff+9,pos+2,yoff+11);
	renderLine(dpy,pos+3,yoff+10,pos+3,yoff+11);
    }

    /**
     * Draw the slider (line and endpoints)
     */
    private void drawSlider()
    {
	Graphics dpy = getGraphics();

	setRenderColor(dpy,Color.black);
	renderLine(dpy,10,yoff,10,yoff+5);
	renderLine(dpy,10,yoff+5,width-9,yoff+5);
	renderLine(dpy,width-9,yoff+5,width-9,yoff);

	drawTags(dpy);

	setRenderColor(dpy,Color.black);
	drawPointer(dpy);
    }

    /**
     * Move pointer to indicated value
     *
     * @param d value to position pointer at in [0,R/2]
     */
    private void moveToValue(double d)
    {
	Graphics dpy = getGraphics();

	setRenderColor(dpy,getBackground());
	drawPointer(dpy);
	setRenderColor(dpy,Color.black);
	pointer_value = d;
	drawPointer(dpy);
    }

    /**
     * Response to the user clicking the mouse
     *
     * @param pos the x-coord of the mouse click (pixels)
     */
    private void handleClickAtPos(int pos)
    {
	double d;

	pos -= 10;
	d = current_tree.getIntervalRange().getTo();
	d = (d*pos)/(width-20);
	moveToValue(d);

	setTree(d);
    }


    /**
     * Update display from buffer image
     */
    public void paint(Graphics g)
    {
	super.paint(g);

	if (!has_drawn) {
	    has_drawn = true;
	    drawSlider();
	}
    }


    /**
     * Display a new tree in the tree view
     *
     * @param i the index of the new tree to display
     */   
    private void setTree(int i)
    {
	double from,to;
	ScannerTrees s = current_tree.getScannerTrees();
	
	if (i>=s.size() || i<0) return;

	if (aListener!=null) {
	    aListener.animationNew((AnimationTree)(s.elementAt(i)));
	    aListener.animationFlush();
	}

	from = ((AnimationTree)(s.elementAt(i))).getPosition();
	to = (i == (s.size()-1))?
	    (current_tree.getIntervalRange().getTo()):
	    ((AnimationTree)(s.elementAt(i+1))).getPosition();

	current_position = i;
	moveToValue(from+(to-from)/2);
    }

    /**
     * Display a new tree in the tree view corresponding to a
     * given position on [0,R/2]
     *
     * @param d the position of the tree to display
     */
    private void setTree(double d)
    {
	ScannerTrees s = current_tree.getScannerTrees();
	VectorIterator vi = new VectorIterator(s);
	int i = -1;
	while (vi.hasNext()) {
	    AnimationTree a = (AnimationTree)(vi.next());
	    if (a.getPosition() > d) break;
	    i++;
	}

	if (i == current_position) return;
	current_position = i;

	if (aListener!=null) {
	    aListener.animationNew((AnimationTree)(s.elementAt(i)));
	    aListener.animationFlush();
	}
    }

    /// Current tree index
    protected int current_position;

    
    /**
     * Jump to next tree
     *
     * @param forward if true jump forward, else jump backwards
     */
    public void nextTree(boolean forward)
    {
	ScannerTrees s = current_tree.getScannerTrees();
	int new_position = forward?(current_position+1):(current_position-1);
	
	new_position = Math.max(0,new_position);
	new_position = Math.min(s.size()-1,new_position);

	if (new_position != current_position) {
	    setTree(new_position);
	}
    }


    /**
     * New animation - set current tree
     *
     * @param s the new animation tree
     */
    public void animationNew(AnimationTree s)
    {
	clearToBack();
	current_tree = s;

	drawSlider();
	moveToValue(0.0);
	if (cListener != null) cListener.setButtonState(true);
	setTree(0);
    }

    /**
     * React to click in control panel
     *
     * @param forward forward step or backward steps
     */
    public boolean animationStep(boolean forward)
    { 
	nextTree(forward);
	return false;
    };


    /**
     * Show first tree
     */
    public void animationRewind() { setTree(0); };


    /**
     * Show last tree
     */
    public void animationFlush() { setTree(current_tree.getScannerTrees().size()-1); }

    /**
     * Ignore
     */
    public void animationPrepare() {};

    /**
     * Ignore
     */
    public void animationStart() {};

    /**
     * Ignore
     */
    public void animationStop() {};

    /**
     * Ignore
     */
    public void animationSpeed(int s) {};


    /**
     * React on the usermoving the slider with the mouse
     */
    private class smallMouseListener extends MouseAdapter
    {

	/**
	 * Move to pixel where mouse was clicked
	 */
	public void mouseClicked(MouseEvent e)
	{
	    int x;
	    Dimension d = getSize();

	    x = e.getX();
	    if (x>=10 && x<=(d.width-10))
		handleClickAtPos(x);
	}
    }
}
