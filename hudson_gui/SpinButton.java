
import java.awt.*;
import java.awt.event.*;

/**
 * A classic spinbutton (is not standard in java 1.1 awt)
 *
 * @author Anders Mikkelsen
 */
public class SpinButton extends Panel
{
    /// The text part of the spinbutton
    TextField tfText;

    /// A spin listener listening on this spin button
    SpinListener sListener;

    /// The last value emitted to the listener
    double last_value;

    /// Button border color
    Color bright,dark;

    /// Button dimension
    static int bwidth = 12;
    static int bheight = 8;
    static int byoffset = 3;

    /// Value settings
    double minv, maxv, stepv;
    boolean asInt, exponential;

    /// Optional postfix string
    String postfix;

    /**
     * Make the text field
     *
     * @param s initial text
     * @param cols number of columns
     */   
    private TextField makeTextField(String s, int cols)
    {
	TextField t = new TextField(s,cols);
	t.setBackground(GUIConfig.buttonbgcolor);
	return t;
    }

    /**
     * Make a spinbutton
     *
     * @param cols number of visible text colums (chars)
     * @param val initial value
     * @param min minimum allowed value
     * @param max maximum allowed value
     * @param step step size (multiplicity if exponential)
     * @param asInt if true, the spinbutton will only use integer values
     */
    public SpinButton(int cols, double val, double min, double max, double step, boolean asInt)
    {

	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();

	minv = min;
	maxv = max;
	stepv = step;
	postfix = "";
	last_value = val;
	this.asInt = asInt;

	setLayout(gridbag);

	bright = GUIConfig.buttonbgcolor.brighter();
	dark   = GUIConfig.buttonbgcolor.darker();

	gbc.insets = new Insets(0,0,0,bwidth+1);

	if (asInt)
	    tfText = new TextField((int)(val)+postfix,cols);
	else
	    tfText = new TextField(val+postfix,cols);

	tfText.setBackground(GUIConfig.buttonbgcolor);
	tfText.addFocusListener(new smallFocusListener());
	add(tfText, gbc);

	addMouseListener(new smallMouseListener());
    }

    /**
     * Set a postfix character (eg. %)
     *
     * @param s the postfix character
     */
    public void setPost(char s) 
    { 
	postfix = (new Character(s)).toString();
	validateText();
    }

    /**
     * Activate exponential increase
     *
     * @param exp if true, turn exponential stepping on
     */
    public void setExp(boolean exp) { exponential = exp; }
    
    /**
     * Get the current value of this spin button
     *
     * @return the current value of this spin button
     */
    public double getValue() { return validateText(); }

    /**
     * Add an amout to the current value of this spin button
     *
     * @param v the amount to add
     */
    protected double addValue(double v)
    {
	double i,nv;
	String s = tfText.getText();

	if (s.length()>0 && postfix.length()>0 &&
	    s.charAt(s.length()-1) == postfix.charAt(0))
	    s = s.substring(0,s.length()-1);

	try {
	    i = (new Double(s)).doubleValue();//Integer.parseInt(s);
	} catch (Exception e) { 
	    if (asInt)
		tfText.setText((int)minv+postfix);
	    else 
		tfText.setText(minv+postfix); 
	    return minv; }
	
	nv = i+v;
	if (nv < minv) nv = minv;
	if (nv > maxv) nv = maxv;

	if (asInt)
	    tfText.setText((int)nv+postfix);
	else
	    tfText.setText(nv+postfix);

	if (nv!=last_value && sListener!=null)
	    sListener.spinValueChanged(nv);

	last_value = nv;

	return nv;
    }

    /**
     * Validate that the current value is legal and within its bounds
     */
    protected double validateText() { return addValue(0); }

    /**
     * Increase the current value by an amount
     * 
     * @param amount the amount to increase the current value by
     */
    public void increaseValue(int amount) { addValue(amount); }

    /**
     * Decrease the current value by an amount
     * 
     * @param amount the amount to decrease the current value by
     */
    public void decreaseValue(int amount) { addValue(-amount); }

    /**
     * Increase the current value by the step size
     */
    public void stepUp()
    { 
	double v = validateText();
	if (exponential) {
	    if (v==0.0)
		addValue(1.0);
	    else
		addValue(stepv*v - v);
	} else
	    addValue(stepv);
    }

    /**
     * Decrease the current value by the step size
     */
    public void stepDown() 
    { 
	double v = validateText();
	if (exponential) {
	    double n = v/stepv;
	    if (n<1.0) n=0.0;
	    addValue(n-v);
	} else
	    addValue(-stepv);
    }

    /**
     * Set the listener for this spin button
     *
     * @param sl the new spin listener for this button
     */
    public void setSpinListener(SpinListener sl) { sListener = sl; }


    /**
     * Set pen color
     *
     * @param grp graphics for this panel
     * @param c new pen color
     */
    private void setRenderColor(Graphics grp, Color c)
    {
	grp.setColor(c);
    }

    /**
     * Draw a line
     *
     * @param grp graphics for this panel
     * @param x1 top left x-coord
     * @param y1 top left y-coord
     * @param x2 bottom right x-coord
     * @param y2 bottom right y-coord
     */
    private void renderLine(Graphics grp, int x1, int y1, int x2, int y2)
    {
	grp.drawLine(x1,y1,x2,y2);	
    }

    /**
     * Draw a filled rectangle
     *
     * @param grp graphics for this panel
     * @param x top left x-coord
     * @param y top left y-coord
     * @param width width of rectangle
     * @param height height of rectangle
     */
    private void renderFilledRect(Graphics grp, int x, int y, int width, int height)
    {
	grp.fillRect(x,y,width,height);
    }

    /**
     * Draw a small arrow pointing up or down
     *
     * @param grp graphics for this panel
     * @param upper if true point upwards, else downwards
     */
    private void paintArrow(Graphics grp, boolean upper)
    {
	Dimension d = getSize();
	int len, x, y, i, h;

	setRenderColor(grp, Color.black);
	h = 4;
	x = d.width - bwidth - 1;
	len = upper?1:(h-1)*2+1;
	x += bwidth/2-len/2;
	y = upper?byoffset:(d.height-bheight-byoffset);
	y += (bheight - h)/2;

	for (i=0; i<h; i++) {
	    renderLine(grp,x,y,x+len,y);
	    x += upper?-1:1;
	    len += upper?2:-2;
	    y++;
	}
    }

    /**
     * Draw a button with an arrow on it
     *
     * @param grp graphics for this panel
     * @param upper if true point arrow upwards, else downwards
     * @param up if true have the button look like it is not pressed, else make it look like it is pressed
     */
    private void paintButton(Graphics grp, boolean upper, boolean up)
    {	
	Dimension d = getSize();
	int x1,y1;

	if (grp == null) grp = getGraphics();

	x1 =  (int)d.width - bwidth - 1;
	y1 = upper?byoffset:(d.height-bheight-byoffset);

	setRenderColor(grp,GUIConfig.buttonbgcolor);
	renderFilledRect(grp,x1,y1,bwidth,bheight);

	setRenderColor(grp,up?bright:dark);
	renderLine(grp,x1,y1,x1+bwidth,y1);
	renderLine(grp,x1,y1,x1,y1+bheight);

	setRenderColor(grp,up?dark:bright);
	renderLine(grp,x1+bwidth,y1+1,x1+bwidth,y1+bheight);
	renderLine(grp,x1+1,y1+bheight,x1+bwidth,y1+bheight);
	
	paintArrow(grp,upper);
    }

    /**
     * React to re-paint request
     */
    public void paint(Graphics g)
    {
	Dimension d = getSize();
	bheight = d.height/2-byoffset-1;

	paintButton(g,true,true);
	paintButton(g,false,true);
    }

    /**
     * Validate text when this panel looses focus
     */
    private class smallFocusListener extends FocusAdapter
    {
	public void focusLost(FocusEvent e) { validateText(); }	
    }

    /**
     * Handle mouse clicks on the up/down arrows
     */
    private class smallMouseListener extends MouseAdapter
    {
	/// Top button is down
	private boolean top_is_down = false;

	/// Bottom button is down
	private boolean bottom_is_down = false;

	/// Mouse has exited button
	private boolean exited = true;

	/**
	 * Is there a button under this coordinate
	 *
	 * @param x x-coord (pixel)
	 * @param y y-coord (pixel)
	 * @return 1 if over top button, 2 if over bottom button, 0 otherwise
	 */
	int buttonUnder(int x, int y)
	{
	    Dimension d = getSize();
	    
	    if (x > (d.width-bwidth) && x<d.width) {
		if (y>2 && y<(bheight+2))
		    return 1;
		if (y>(d.height-bheight-2) && y<(d.height-2))
		    return 2;
	    }
	    return 0;	    
	}

	/**
	 * React on the mouse being pressed
	 */
	public void mousePressed(MouseEvent e)
	{
	    switch (buttonUnder(e.getX(),e.getY())) {
	    case 1:
		top_is_down = true;
		paintButton((Graphics)null,true,false);
		break;
	    case 2:
		bottom_is_down = true;
		paintButton((Graphics)null,false,false);
		break;
	    }
	}

	/**
	 * React on the mouse being released
	 */
	public void mouseReleased(MouseEvent e) 
	{	    
	    if (top_is_down) paintButton((Graphics)null,true,true);
	    if (bottom_is_down) paintButton((Graphics)null,false,true);

	    switch (buttonUnder(e.getX(),e.getY())) {
	    case 1:
		if (top_is_down) stepUp();
		break;
	    case 2:
		if (bottom_is_down) stepDown();
		break;
	    }

	    top_is_down = false;
	    bottom_is_down = false;
	}
    }




}
