
import java.awt.*;
import java.awt.event.*;

/**
 * Button that shows a small icon. The set of possible icons
 * is hard-coded into to the button, so extending the implementation
 * with new icons can be difficult.
 *
 * @author Anders Mikkelsen
 */
public class IconButton extends Canvas
{
    /// The possible buttons
    public static int Play     = 0;
    public static int Pause    = 1;
    public static int Stop     = 2;
    public static int Rewind   = 3;
    public static int FForward = 4;
    public static int Step     = 5;
    public static int BackStep = 6;

    /// The type of this button
    private int type;

    /// The button keeps an image reflecting the appearance of the button
    Image image_buffer;

    /// Graphics associated with the image buffer
    Graphics image_graphics;

    /// Width of a button
    int width = 25;

    /// Height of a button
    int height = 25;

    /// Border colors
    Color dark,bright;

    /// Action string associated with this button
    String action_command;
    
    /// Action listener listening on this button
    ActionListener aListener;

    /// Message to show in status bar when mouse moves over this button
    String stat_msg;

    /**
     * Create new icon button
     *
     * @param type type of button (IconButton.Play, IconButton.Pause, ...)
     */
    public IconButton(int type)
    {
	super();
	this.type = type;
	setBackground(GUIConfig.buttonbgcolor);

	dark = getBackground().darker();
	bright = getBackground().brighter();

	addMouseListener(new smallMouseListener());
	is_enabled = true;
	setSize(getPreferredSize());
    }

    /**
     * Set associated action command
     *
     * @param s action command
     */
    public void setActionCommand(String s) { action_command = s; }

    /**
     * Set action listener
     *
     * @param s an action listener
     */
    public void setActionListener(ActionListener s) { aListener = s; }

    
    /**
     * Create buffer image when connecting to native system 
     */
    public void addNotify()
    {
	super.addNotify();

	Dimension d = getSize();
		
	image_buffer = createImage(d.width,d.height);
	image_graphics = image_buffer.getGraphics();
    }

    /**
     * Set status message
     *
     * @param s the status message
     */
    public void setStatusMessage(String s) { stat_msg = s; }

    /**
     * Set pen color
     * 
     * @param c the pen color
     */
    private void setRenderColor(Color c)
    {
	image_graphics.setColor(c);
    }

    /**
     * Draw a line
     *
     * @param x1 top left x-coord
     * @param y1 top left y-coord
     * @param x2 bottom right x-coord
     * @param y2 bottom right y-coord
     */
    private void renderLine(int x1, int y1, int x2, int y2)
    {
	image_graphics.drawLine(x1,y1,x2,y2);	
    }

    /**
     * Draw filled rectangle
     *
     * @param x top left x-coord
     * @param y top left y-coord
     * @param width width of rectangle
     * @param height height of rectangle
     */
    private void renderFilledRect(int x, int y, int width, int height)
    {
	image_graphics.fillRect(x,y,width,height);
    }


    /**
     * Draw the border of a button
     *
     * @param up true if the button is up (has to do with shading
     */
    private void paintBorder(boolean up)
    {	
	Dimension d = getSize();
	int width,height;

	width =  (int)d.width - 1;
	height = (int)d.height - 1;

	setRenderColor(up?bright:dark);
	renderLine(0,0,width,0);
	renderLine(0,0,0,height);

	setRenderColor(up?dark:bright);
	renderLine(width,1,width,height);
	renderLine(1,height,width,height);

	Graphics g = getGraphics();
	if (g!=null)
	    g.drawImage(image_buffer,0,0,this);
    }

    /// Is this button enabled
    private boolean is_enabled;

    /**
     * Render checkered pattern if the button is not enabled
     */
    private void renderChecker()
    {
	Dimension d = getSize();
	int i,j;
	
	if (is_enabled || image_graphics==null) return;
	
	setRenderColor(new Color(90,90,90));
	for (i=1; i<d.height-2; i+=2)
	    for (j=1; j<d.width-2; j+=2)
		renderLine(j,i,j,i);
    }

    /**
     * Erase button face
     */
    private void clearFace()    
    {
	Dimension d = getSize();
	if (image_graphics==null) return;

	setRenderColor(getBackground());
	renderFilledRect(1,1,d.width-2,d.height-2);
    }


    /**
     * Paint a play arrow
     *
     * @param forward true if arrow points left to right
     * @param xoffset offset arrow this number of pixels in x-direction
     */
    private void paintArrow(boolean forward, int xoffset)
    {
	int i,j,x,start,len,width,height;
	Dimension d = getSize();

	int medy = d.height/2;
	int medx = d.width/2 + xoffset;
	
	renderLine(medx-3,medy,medx+4,medy);
	renderLine(medx-3,medy-1,medx+4,medy-1);

	if (forward) {
	    renderLine(medx-3,medy-2,medx+2,medy-2);
	    renderLine(medx-3,medy-3,medx  ,medy-3);
	    renderLine(medx-3,medy-4,medx-2,medy-4);
	    
	    renderLine(medx-3,medy+1,medx+2,medy+1);
	    renderLine(medx-3,medy+2,medx  ,medy+2);
	    renderLine(medx-3,medy+3,medx-2,medy+3);
	} else {
	    renderLine(medx-1,medy-2,medx+4,medy-2);
	    renderLine(medx+1,medy-3,medx+4,medy-3);
	    renderLine(medx+3,medy-4,medx+4,medy-4);
	    
	    renderLine(medx-1,medy+1,medx+4,medy+1);
	    renderLine(medx+1,medy+2,medx+4,medy+2);
	    renderLine(medx+3,medy+3,medx+4,medy+3);
	}
    }

    /**
     * Paint the play button
     */
    private void paintPlay()
    {
	paintArrow(true,0);
    }

    /**
     * Paint step button
     */
    private void paintStep()
    {
	Dimension d = getSize();

	paintArrow(true,0);
	renderFilledRect(d.width/2+3,d.height/2-4,2,8);
    }

    /**
     * Paint back-step button
     */
    private void paintBackStep()
    {
	Dimension d = getSize();

	paintArrow(false,0);
	renderFilledRect(d.width/2-3,d.height/2-4,2,8);
    }

    /**
     * Paint pause button
     */
    private void paintPause() 
    {
	int width, height;
	Dimension d = getSize();

	width = 3;
	height = 8;

	renderFilledRect((int)d.width/2-2-width, (int)(d.height-height)/2, width, height);
	renderFilledRect((int)d.width/2+1, (int)(d.height-height)/2, width, height);
    }

    /**
     * Paint rewind button
     */
    private void paintRewind()
    {
	paintArrow(false,-3);
	paintArrow(false,2);
    }

    /**
     * Paint fast-forward button
     */
    private void paintFForward()
    {
	paintArrow(true,-3);
	paintArrow(true,2);
    }

    /**
     * Paint stop button - not implemented
     */
    private void paintStop() {};
    

    /**
     * Paint the face of this button
     */
    private void paintFace()
    {
	if (image_graphics == null) return;
	setRenderColor(Color.black);

	while (true) {
	    if (type == Play) {
		paintPlay();
		break;
	    }
	    if (type == Pause) {
		paintPause();
		break;
	    }
	    if (type == Stop) {
		paintStop();
		break;
	    }
	    if (type == Rewind) {
		paintRewind();
		break;
	    }
	    if (type == FForward) {
		paintFForward();
		break;
	    }
	    if (type == Step) {
		paintStep();
		break;
	    }
	    if (type == BackStep) {
		paintBackStep();
		break;
	    }
	    break;
	}
    }

    /**
     * Set correct preferred size
     */
    public Dimension getPreferredSize()
    {
	return new Dimension(22,22);
    }

    /**
     * Change enabling
     *
     * @param b true if the button is enabled
     */
    public void setEnabled(boolean b)
    {
	super.setEnabled(b);
	Graphics g = getGraphics();

	is_enabled = b;
	if (b) {
	    clearFace();
	    paintFace();
	} else
	    renderChecker();

	if (g != null)
	    g.drawImage(image_buffer,0,0,this);
    }


    /**
     * Re-paint this button
     */
    public void paint(Graphics g)
    {
	super.paint(g);
	
	if (image_graphics == null) return;

	paintBorder(true);
	paintFace();
	renderChecker();
	
	g.drawImage(image_buffer,0,0,this);
    }


    /**
     * React to mouse events on this button
     */
    private class smallMouseListener extends MouseAdapter
    {
	// Is this button down
	private boolean is_down = false;

	// Has the mouse exited this button
	private boolean exited = true;

	/**
	 * Update status bar
	 */
	public void mouseEntered(MouseEvent e)
	{
	    if (isEnabled() && stat_msg != null)
		StatusBar.setStatus(stat_msg);
	}

	/**
	 * Mouse left
	 */
	public void mouseExited(MouseEvent e)
	{
	    if (is_down) paintBorder(true);
	    is_down = false;
	    exited = true;
	}

	/**
	 * Make button seem pressed
	 */
	public void mousePressed(MouseEvent e)
	{
	    is_down = true;
	    exited = false;
	    paintBorder(false);
	}

	/**
	 * Normalize button - possibly emit click signal
	 */
	public void mouseReleased(MouseEvent e) 
	{	    
	    is_down = false;
	    paintBorder(true);

	    if (aListener != null && !exited)
		aListener.actionPerformed(new ActionEvent(this,
							  ActionEvent.ACTION_PERFORMED,
							  action_command));
	}
    }


}
