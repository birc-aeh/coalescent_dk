
import java.awt.*;

/** 
 * Canvas keeping an off-screen copy of its contents to be
 * able to respond to paint events.
 * 
 * @author Anders Mikkelsen
 */
public class BufferedCanvas extends Canvas
{
    /** 
     * Dimension
     * 
     */
    protected int width, height;

    /** 
     * Buffer image
     */
    protected Image ib;

    /** 
     * Graphics for buffer image
     */
    protected Graphics ig;

    /** 
     * Construct new buffered canvas
     * 
     * @param width the desired width of the new canvas
     * @param height the desired height of the new canvas
     */
    public BufferedCanvas(int width, int height)
    {
	this.width = width;
	this.height = height;
	setSize(width,height);
    }

    /** 
     * Create buffer image when connecting to native code
     * 
     */
    public void addNotify()
    {
        super.addNotify();
	
        ib = createImage(width,height);
        ig = ib.getGraphics();    
    }

    /** 
     * Redraw canvas from buffer image
     * 
     * @param g graphics for this canvas
     */
    public void paint(Graphics g)
    {
        super.paint(g);

        if (ib != null)
            g.drawImage(ib,0,0,this);
    }


    /** 
     * Set pen color
     * 
     * @param g graphics for this canvas
     * @param c new pen color
     */
    protected void setRenderColor(Graphics g, Color c)
    {
	g.setColor(c);
	ig.setColor(c);
    }

    /** 
     * Draw a circle on canvas and buffer image
     * 
     * @param g graphics for this canvas
     * @param x x-coordinate of center
     * @param y y-coordinate of center
     * @param diameter diameter of circle
     */
    protected void renderCircle(Graphics g, int x, int y, int diameter)
    {
	x -= diameter/2;
	y -= diameter/2;
	
	g.drawOval(x,y,diameter,diameter);
	ig.drawOval(x,y,diameter,diameter);
    }
    
    /** 
     * Draw a filled circle
     * 
     * @param g graphics for this canvas
     * @param x x-coordinate of center
     * @param y y-coordinate of center
     * @param diameter diameter
     */
    protected void renderFilledCircle(Graphics g, int x, int y, int diameter)
    {
	x -= diameter/2;
	y -= diameter/2;
	
	g.fillOval(x,y,diameter,diameter);
	ig.fillOval(x,y,diameter,diameter);
    }

    /** 
     * Draw a filled rectangle
     * 
     * @param g graphics for this canvas
     * @param x1 top left x-coordinate
     * @param y1 top left y-coordinate
     * @param x2 bottom right x-coordinate
     * @param y2 bottom right y-coordinate
     */
    protected void renderFilledRect(Graphics g, int x1, int y1, int x2, int y2)
    {
	g.fillRect(x1,y1,x2,y2);
	ig.fillRect(x1,y1,x2,y2);
    }

    /** 
     * Draw a line
     * 
     * @param g graphics for this canvas
     * @param x1 top left x-coordinate
     * @param y1 top left y-coordinate
     * @param x2 bottom right x-coordinate
     * @param y2 bottom right y-coordinate
     */
    protected void renderLine(Graphics g, int x1, int y1, int x2, int y2)
    {
	g.drawLine(x1,y1,x2,y2);
	ig.drawLine(x1,y1,x2,y2);
    }


    /** 
     * Erase to background color
     * 
     */
    protected void clearToBack()
    {
	Graphics g = getGraphics();
	setRenderColor(g,getBackground());
	renderFilledRect(g,0,0,width,height);
    }

}
