
import java.awt.*;
import java.awt.event.*;
import java.util.*;


/**
 * Window for displaying information about nodes.
 *
 * @author Anders Mikkelsen
 */
public class InfoWindow extends BufferedCanvas implements InfoListener
{
    /// Current offset in display (in pixels)
    private int line;

    /// Font for displaying information
    Font f;

    /// Height of letters in selected font
    int font_height;

    /// Light gray - currently unused
    private Color lgray = Color.gray.brighter();

    /**
     * Make new infowindow with indicated width and height
     *
     * @param width  desired width of info window
     * @param height desired height of info window
     */
    public InfoWindow(int width, int height)
    {
	super(width,height);
  	setBackground(Color.white);
    }

    /**
     * Allocates the buffer image when connecting to native window code
     */
    public void addNotify()
    {
	super.addNotify();

	f = new Font("SansSerif", Font.BOLD, 10);
	ig.setFont(f);

	font_height = ig.getFontMetrics().getHeight();
    }

    /**
     * Render string on canvas and backing image
     *
     * @param s the string to render
     */
    private void renderString(Graphics grp, String s)
    {
	line += font_height;

	grp.drawString(s,5,line);
	ig.drawString(s,5,line);
    }

    /**
     * Render an interval bar. The interval bar has a red part, a green
     * part and a white part. Only the red and green parts are specified.
     * The white part is "the rest". Interval must be in range [0:max].
     *
     * @param x      x-coordinate of upper left corner
     * @param y      y-coordinate of upper left corner
     * @param width  interval bar width
     * @param height interval bar height
     * @param max    max value of any interval point
     * @param red    the red intervals
     * @param green  the green intervals
     */
    private void renderIntervals(Graphics grp, int x, int y, int width, int height,
				 Interval range, IntervalList red, IntervalList green, IntervalList grey)
    {
	double ratio, bp, max;
	VectorIterator vi;
	Interval i;
	int from,to,yoff;

	if (range == null) return;
	max = range.getTo();

	setRenderColor(grp,Color.black);
	renderRect(grp,x,y,width,height);
	ratio = (double)width/max;

	if (red != null) {
	    vi = new VectorIterator(red);
	    while (vi.hasNext()) {
		i = (Interval)(vi.next());
		from = (int)(x + ratio*i.getFrom());
		to = (int)(x + ratio*i.getTo());

		setRenderColor(grp,Color.red);
		renderFilledRect(grp,from, y+1, to-from, height-1);
		setRenderColor(grp,Color.black);
		renderRect(grp,from, y, to-from, height);
	    }
	}

	if (green != null) {
	    vi = new VectorIterator(green);
	    while (vi.hasNext()) {
		i = (Interval)(vi.next());
		from = (int)(x + ratio*i.getFrom());
		to = (int)(x + ratio*i.getTo());

		setRenderColor(grp,Color.green);
		renderFilledRect(grp, from, y+1, to-from, height-1);
		setRenderColor(grp,Color.black);
		renderRect(grp, from, y, to-from, height);
	    }
	}


//  	if (grey != null) {
//  	    vi = new VectorIterator(grey);
//  	    while (vi.hasNext()) {
//  		i = (Interval)(vi.next());
//  		from = (int)(x + ratio*i.getFrom());
//  		to = (int)(x + ratio*i.getTo());

//  		setRenderColor(grp,lgray);
//  		renderFilledRect(grp, from, y+1, to-from, height-1);
//  		setRenderColor(grp,Color.black);
//  		renderRect(grp, from, y, to-from, height);
//  	    }
//  	}


	if (red != null) {
	    vi = new VectorIterator(red.getBreakPoints());
	    while (vi.hasNext()) {
		bp = ((Double)(vi.next())).doubleValue();
		from = (int)(x + ratio*bp);
		yoff = y+height;

		setRenderColor(grp,Color.black);
		renderLine(grp,from,y-1,from,y+height+1);

		renderLine(grp,from,  yoff+3,from,  yoff+10);
		renderLine(grp,from-1,yoff+4,from-1,yoff+10);
		renderLine(grp,from-2,yoff+5,from-2,yoff+7);
		renderLine(grp,from-3,yoff+6,from-3,yoff+7);

		renderLine(grp,from+1,yoff+4,from+1,yoff+10);
		renderLine(grp,from+2,yoff+5,from+2,yoff+7);
		renderLine(grp,from+3,yoff+6,from+3,yoff+7);

		//renderRect(from-2,y-2,2,height+4,Color.black);
	    }
	}
    }

    /**
     * Update contents of info window, ie. display information about
     * a new node. NOTE: current version can only handle coalescent nodes
     * and recombination nodes. Patch this function to accomodate for
     * other node types.
     *
     * @param n      the node to display info about
     * @param range  the range of intervals. The first coordinate of this
     *               range should always be 0.0
     */
    public void update(AnimationTreeNode n, Interval range, boolean selection)
    {
	line = 0;
	clearToBack();
	AnimationTreeEdge padre, mama, tmp;
	int k;
	Graphics grp = getGraphics();
	grp.setFont(f);

	StringTokenizer st = new StringTokenizer(n.description,"\\",false);
	setRenderColor(grp,Color.black);
	if (st.hasMoreTokens())
	    renderString(grp,st.nextToken());
	while (st.hasMoreTokens())
	    renderString(grp,st.nextToken().substring(1));

	if (selection) return;

	line += 5;
	switch (n.getOuts().size()) {
	case 2:
	    padre = (AnimationTreeEdge)(n.getOuts().elementAt(0));
	    mama =  (AnimationTreeEdge)(n.getOuts().elementAt(1));
	    if (padre.dest.x > mama.dest.x) {
		tmp = padre;
		padre = mama;
		mama = tmp;
	    }

	    k = (width-15)/2;
	    renderIntervals(grp,5,line,k,10,range,padre.getIntervals(),new IntervalList(),padre.getGreyIntervals());
	    renderIntervals(grp,10+k,line,k,10,range,mama.getIntervals(), new IntervalList(),mama.getGreyIntervals());

	    line += 15;	    
	    renderIntervals(grp,(width-k)/2,line,k,10,range,
			    n.getIntervals(),n.getAncestorIntervals(),n.getGreyIntervals());
	    break;
	default:
	    k = (width-15)/2;
	    line += 15;	    
	    renderIntervals(grp,(width-k)/2,line,k,10,range,
			    n.getIntervals(),n.getAncestorIntervals(),n.getGreyIntervals());
	    break;
	}
    }


}
