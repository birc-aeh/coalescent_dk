
import java.awt.*;
import java.util.*;
import java.awt.event.*;


/**
 * Panel holding the main animation card and tree view card
 *
 * @author Anders Mikkelsen
 */
public class AnimationCards extends Panel
{
    /// The card layout for this panel
    protected CardLayout cly;

    /// Inner panel holding the cards
    protected Panel cards;

    /// The main animation card
    protected AnimationCard ani;

    /// The card with the individual trees
    protected ScannerCard sc;

    /// The max width of a "faneblads-fane" - based on the actual font dimensions
    protected int fan_width;

    /// The max height of a "faneblads-fane" - based on the actual font dimensions
    protected int fan_height;

    /// Vector holding the text on the "faneblads-fanerne"
    protected Vector fans;

    /// Vector holding the width of the individual "faneblads-faner"
    protected Vector fan_widths;

    /// Darker than the default button background
    Color dark =   GUIConfig.buttonbgcolor.darker();

    /// Brighter than the default button background
    Color bright = GUIConfig.buttonbgcolor.brighter();

    /// The currently selected "faneblad"
    protected int selected;


    /**
     * Construct a main view with the indicated width and height
     *
     * @param width  the desired width
     * @param height the desired height
     */
    public AnimationCards(int width, int height)
    {
	setLayout(new GridBagLayout());

	fans = new Vector();
	fan_widths = new Vector();
	fans.addElement("Animation");
	fans.addElement("Trees");
	selected = 0;
	addMouseListener(new smallMouseListener());

	cards = new Panel();
	cly = new CardLayout();
	cards.setLayout(cly);

	ani = new AnimationCard(width,height);
	cards.add(ani,(String)fans.elementAt(0));

  	sc = new ScannerCard(width,height);
	cards.add(sc,(String)fans.elementAt(1));	
    }


    /**
     * Set an info listener for this main view
     *
     * @param i  the info listener 
     */
    public void setInfoListener(InfoListener i)
    {
	if (ani != null)
	    ani.setInfoListener(i);
	if (sc != null)
	    sc.setInfoListener(i);
    }

    /**
     * Get all animation listeners in this main view
     *
     * @return all animation listeners in the main view
     */
    public Vector getAnimationListeners()
    {
	Vector res = new Vector();
	res.addElement(ani.getAnimationListener());
	res.addElement(sc.getAnimationListener());
	return res;
    }

    /**
     * Get text dimension when connected to native system. The individual
     * cards cannot be added until now because their dimensions are 
     * depending on the size of the "faneblads-fanerne"
     */
    public void addNotify()
    {
	super.addNotify();
	int i,w;

	FontMetrics fm = getGraphics().getFontMetrics();
	
	for (i=0; i<fans.size(); i++) {
	    w = fm.stringWidth((String)fans.elementAt(i));
	    fan_widths.addElement(new Integer(w));
	    fan_width = Math.max(fan_width,w);
	}
	fan_width += 16;
	fan_height = fm.getAscent() + 12;

	GridBagConstraints gbc = new GridBagConstraints();
	gbc.fill = GridBagConstraints.BOTH;
	gbc.weightx = 1.0;
	gbc.weighty = 1.0;
	gbc.insets = new Insets(fan_height,3,3,3);
	add(cards,gbc);
    }

    /**
     * Draw a neatly shadowed box with the indicated dimensions. The special
     * kind parameter determines what edges of the box to draw: set bit 1 to have the
     * left edge drawn, bit 2 to have the top edge drawn, bit 3 to have the right
     * edge drawn, and bit 4 to have the bottom edge drawn.
     *
     * @param g       the graphics for this panel
     * @param x1      top left x-coord
     * @param y1      top left y-coord
     * @param width   width of the box
     * @param height  height of the box
     * @param kind    the edges to include
     */
    private void paintShadowBox(Graphics g, int x1, int y1, int width, int height, int kind)
    {
	int x2,y2;

	x2 = x1+width-1;
	y2 = y1+height-1;

	g.setColor(bright);
	if ((kind & 2) != 0) g.drawLine(x1,y1,x2,y1);
	if ((kind & 1) != 0) g.drawLine(x1,y1,x1,y2);
	if ((kind & 4) != 0) g.drawLine(x2-1,y1+1,x2-1,y2-1);
	if ((kind & 8) != 0) g.drawLine(x1+1,y2-1,x2-1,y2-1);

	g.setColor(dark);
	if ((kind & 2) != 0) g.drawLine(x1+1,y1+1,x2-1,y1+1);
	if ((kind & 1) != 0) g.drawLine(x1+1,y1+1,x1+1,y2-1);
	if ((kind & 4) != 0) g.drawLine(x2,y1,x2,y2);
	if ((kind & 8) != 0) g.drawLine(x1,y2,x2,y2);
    }

    /**
     * Paint the currently selected "faneblads-fane"
     *
     * @param g the graphics for this panel
     */
    public void paintSelectedFan(Graphics g)
    {
	int i,w,offset;

	offset = (fan_width+5)*selected;

	g.setColor(GUIConfig.bgcolor);
	g.fillRect(offset+2,2,fan_width-4,fan_height+8);
	    
	g.setColor(Color.white);	
	g.fillRect(offset+3,3,fan_width-6,fan_height+6);

	paintShadowBox(g,offset,0,fan_width,fan_height-1,1|2|4);

	g.setColor(Color.black);
	w = ((Integer)(fan_widths.elementAt(selected))).intValue();
	g.drawString((String)(fans.elementAt(selected)), offset + (fan_width-w)/2, fan_height-6);
    }


    /**
     * Paint all fans
     *
     * @param g the graphics for this panel
     */
    public void paintFans(Graphics g)
    {
	int i,w,offset;

	offset = 0;
	for (i=0; i<fans.size(); i++) {

	    g.setColor(GUIConfig.buttonbgcolor);
	    g.fillRect(offset+3,3,fan_width-6,fan_height+6);

	    paintShadowBox(g,offset,0,fan_width,fan_height-1,1|2|4);

	    g.setColor(Color.black);
	    w = ((Integer)(fan_widths.elementAt(i))).intValue();
	    g.drawString((String)(fans.elementAt(i)), offset + (fan_width-w)/2, fan_height-6);

	    offset += fan_width + 5;
	}
	

    }

    /**
     * Select an new fan, ie. bring a new card to front
     *
     * @param i the card to bring to front
     */
    private void setSelected(int i)
    {
	Dimension d = getSize();
	int width,height;
	Graphics g = getGraphics();

	selected = i;
	cly.show(cards,(String)fans.elementAt(selected));
	
	width = (int)d.width;
	height = (int)d.height;

	paintFans(g);
	paintShadowBox(g,0,fan_height-3,width,height, 2);
	paintSelectedFan(g);
    }


    /**
     * Paint "faneblade" and boxes around the cards
     *
     * @param g the graphics for this panel
     */
    public void paint(Graphics g)
    {
	super.paint(g);

	Dimension d = getSize();
	int width,height;

	width = (int)d.width;
	height = (int)d.height;
	
	paintFans(g);
	paintShadowBox(g,0,fan_height-3,width,height-fan_height+3, 1|2|4|8);
	paintSelectedFan(g);
    }


    /**
     * A small class to listen for mouse clicks in this panel - to 
     * be able to allow the user to select a new card.
     */
    private class smallMouseListener extends MouseAdapter
    {

	/**
	 * React to the mouse being clicked
	 *
	 * @param e the mouse event
	 */
	public void mouseClicked(MouseEvent e)
	{
	    int ix;

	    if (e.getY() < fan_height) {
		ix = e.getX()/(fan_width+5);
		if (ix >= fans.size() || ix==selected) return;
		setSelected(ix);	       
	    }
	}

    }

}
