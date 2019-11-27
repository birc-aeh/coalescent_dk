

import java.awt.*;

/**
 * A canvas with a buffer image. All drawing on the canvas is reflected in the
 * buffer image so as to be able to respond to re-paint events.
 *
 * @author Anders Mikkelsen
 */
public class BufferedCanvas extends Canvas
{
    /// Dimension of the canvas
    protected int width, height;

    /// Image buffer
    protected Image ib;

    /// Graphics handle associated with the image buffer
    protected Graphics ig;


    /**
     * New buffered canvas
     *
     * @param width desired width of the canvas
     * @param height desired height of the canvas
     */
    public BufferedCanvas(int width, int height)
    {
	this.width = width;
	this.height = height;
	setSize(width,height);
    }


    /**
     * Create buffer image when connection to native system is established
     */
    public void addNotify()
    {
        super.addNotify();
	
        ib = createImage(width,height);
        ig = ib.getGraphics();    
    }

    /**
     * Copy buffer image to canvas
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
     * @param g  graphics for the canvas
     * @param c  new pen color
     */
    protected void setRenderColor(Graphics g, Color c)
    {
	g.setColor(c);
	ig.setColor(c);
    }

    /**
     * Draw a circle
     *
     * @param g graphics for the canvas
     * @param x x-coord of center of circle
     * @param y y-coord of center of circle
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
     * @param g graphics for the canvas
     * @param x x-coord of center of circle
     * @param y y-coord of center of circle
     * @param diameter diameter of circle
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
     * @param g graphics for the canvas
     * @param x1 upper left x-coord
     * @param y1 upper left y-coord
     * @param x2 lower right x-coord
     * @param y2 lower right y-coord
     */
    protected void renderFilledRect(Graphics g, int x1, int y1, int x2, int y2)
    {
	g.fillRect(x1,y1,x2,y2);
	ig.fillRect(x1,y1,x2,y2);
    }


    /**
     * Draw a rectangle
     *
     * @param g graphics for the canvas
     * @param x1 upper left x-coord
     * @param y1 upper left y-coord
     * @param x2 lower right x-coord
     * @param y2 lower right y-coord
     */
    protected void renderRect(Graphics g, int x1, int y1, int x2, int y2)
    {
	g.drawRect(x1,y1,x2,y2);
	ig.drawRect(x1,y1,x2,y2);
    }


    /**
     * Draw a line
     *
     * @param g graphics for the canvas
     * @param x1 upper left x-coord
     * @param y1 upper left y-coord
     * @param x2 lower right x-coord
     * @param y2 lower right y-coord
     */
    protected void renderLine(Graphics g, int x1, int y1, int x2, int y2)
    {
	g.drawLine(x1,y1,x2,y2);
	ig.drawLine(x1,y1,x2,y2);
    }


    /**
     * Erase all - fill with background color
     */
    protected void clearToBack()
    {
	Graphics g = getGraphics();
	setRenderColor(g,getBackground());
	renderFilledRect(g,0,0,width,height);
    }

}
