

import java.awt.*;
import java.awt.event.*;

/** 
 * An implementation of a spin button (not included in the Java 1.1 AWT)
 * 
 * @author Anders Mikkelsen
 */
public class SpinButton extends Panel
{
    /** 
     * The text part of the spin button
     * 
     */
    TextField tfText;

    /** 
     * A spin listener listening on this spin button
     * 
     */
    SpinListener sListener;

    /** 
     * Identification string for this spin button - passed to the spin
     * listener when the value changes
     * 
     */
    String id;

    /** 
     * The value of this spin button the last time it was validated - the
     * listener is only informed if a new value of the button is different
     * from this value
     * 
     */
    double last_value;

    /** 
     * Border colors of the buttons
     * 
     */
    Color bright,dark;

    /** 
     * Width of a button
     * 
     */
    static int bwidth = 12;

    /** 
     * Height of a button
     * 
     */
    static int bheight = 8;

    /** 
     * Button offset from top and bottom
     * 
     */
    static int byoffset = 3;

    /** 
     * Value boundary
     * 
     */
    double minv, maxv, stepv;

    /** 
     * String to postfix the value by
     * 
     */
    String postfix;

    /** 
     * True if the button value must be integer
     * 
     */
    boolean asInt, exponential;

    /** 
     * Make a text field
     * 
     * @param s initial text in the field
     * @param cols number of visible columns in the field
     * @return the new text field
     */
    private TextField makeTextField(String s, int cols)
    {
	TextField t = new TextField(s,cols);
	t.setBackground(GUIConfig.buttonbgcolor);
	return t;
    }

    /** 
     * Make a new spin button
     * 
     * @param cols number of visible columns
     * @param val initial value
     * @param min minimum value
     * @param max maximum value
     * @param step step size
     * @param asInt true if the value must be integer, false otherwise
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
     * Set a postfix character for this spin button
     * 
     * @param s the postfix character
     */
    public void setPost(char s) 
    { 
	postfix = (new Character(s)).toString();
	validateText();
    }

    /** 
     * Set exponential stepping
     * 
     * @param exp true if stepping should be exponential, false otherwise
     */
    public void setExp(boolean exp) { exponential = exp; }
    
    /** 
     * Get the current value of this button
     * 
     * @return the current value of this button
     */
    public double getValue() { return validateText(); }

    /** 
     * Add an amount to the current value
     * 
     * @param v the amount to add
     * @return the new value of the button
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
	    sListener.spinValueChanged(id,nv);

	last_value = nv;

	return nv;
    }

    /** 
     * Validate this spinbutton, ie. check that the text field contains
     * only valid characters and that the value is within the boundaries
     * 
     * @return the current value of the button
     */
    protected double validateText() { return addValue(0); }

    /** 
     * Increase the value of the spin button by an amount
     * 
     * @param amount the amount to increase the value by
     */
    public void increaseValue(int amount) { addValue(amount); }

    /** 
     * Subtract an amount from the current value of this spin button
     * 
     * @param amount the amount to subtract
     */
    public void decreaseValue(int amount) { addValue(-amount); }

    /** 
     * Add step amount to the current value of this spin button
     * 
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
     * Subtract step amount from the current value of this spin button
     * 
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
     * Set a spin listener for this spin button
     * 
     * @param id 
     * @param sl 
     */
    public void setSpinListener(String id, SpinListener sl) 
    { 
	this.id = id;
	sListener = sl; 
    }

    /** 
     * Currently does nothing
     * 
     */
    public void addNotify()
    {
	super.addNotify();
    }

    /** 
     * Set pen color
     * 
     * @param grp graphics for this panel
     * @param c the new pen color
     */
    private void setRenderColor(Graphics grp, Color c)
    {
	grp.setColor(c);
    }

    /** 
     * Draw a line
     * 
     * @param grp graphics for this panel
     * @param x1 top left x-coordinate
     * @param y1 top left y-coordinate
     * @param x2 bottom right x-coordinate
     * @param y2 bottom right y-coordinate
     */
    private void renderLine(Graphics grp, int x1, int y1, int x2, int y2)
    {
	grp.drawLine(x1,y1,x2,y2);	
    }

    /** 
     * Draw a filled rectangle
     * 
     * @param grp graphics for this panel
     * @param x top left x-coordinate
     * @param y top left y-coordinate
     * @param width width of the rectangle
     * @param height height of the rectangle
     */
    private void renderFilledRect(Graphics grp, int x, int y, int width, int height)
    {
	grp.fillRect(x,y,width,height);
    }

    /** 
     * Draw an arrow (on a button)
     * 
     * @param grp graphics for this panel
     * @param upper true if the arrow is the top arrow, false otherwise
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
     * Draw a button
     * 
     * @param grp graphics for this panel
     * @param upper true if this is the upper button, false if it the lower
     * @param up false if the button should appear pressed, false otherwise
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
     * Callback - draw the buttons
     * 
     * @param g graphics for this panel
     */
    public void paint(Graphics g)
    {
	Dimension d = getSize();
	bheight = d.height/2-byoffset-1;

	paintButton(g,true,true);
	paintButton(g,false,true);
    }

    private class smallFocusListener extends FocusAdapter
    {
        /** 
         * Callback - called when the text field looses focus
         * 
         * @param e focus event
         */
	public void focusLost(FocusEvent e) { validateText(); }	
    }



    private class smallMouseListener extends MouseAdapter
    {
        /** 
         * True if the top button is currently pressed
         * 
         */
	private boolean top_is_down = false;

        /** 
         * True if the bottom button is currently down
         * 
         */
	private boolean bottom_is_down = false;

        /** 
         * True if the mouse has left both buttons
         * 
         */
	private boolean exited = true;

        /** 
         * Get the ID of the button on a given x,y coordinate
         * 
         * @param x x-coordinate
         * @param y y-coordinate
         * @return 0 if no button is on the coord., 1 if the top button is on 
         *     the coord. and 2 if the bottom button is on the coord.
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
         * Callback - the mouse was pressed
         * 
         * @param e mouse event
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
         * Callback - the mouse was released
         * 
         * @param e mouse event
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
