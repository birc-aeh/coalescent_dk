
import java.awt.*;

/**
 * The Legend panel
 *
 * @author Anders Mikkelsen
 */
public class Legends extends BufferedCanvas implements LegendListener
{
    /// Font used by this legend
    Font f;

    /// Height of letters in the font used
    int font_height;

    /// Number of colors allocated by this legend
    int num_colors;

    /// Colors allocated by this legend
    Color color[];

    /// Offset entries by this amount (updated as entries are rendered)
    int yoffset;

    /**
     * Create legends panel
     *
     * @param width desired width of panel
     * @param height desired height of panel
     */
    public Legends(int width, int height)
    {
	super(width,height);
	setBackground(Color.white);
	initColors();
	yoffset = 1;
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
	font_height = Math.max(font_height,12);
	
	clearLegend();
    }

    /**
     * Initialize colors - THESE SHOULD BE THE SAME AS THE ONES ALLOCATED
     * BY THE MAIN ANIMATION!!
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
     * Get a color from the color array
     *
     * @param i the desired color index
     * @return the color at index i
     */
    private Color getColor(int i)
    {
	if (i<num_colors && i>=0)
	    return color[i];
	return Color.black;
    }

    /**
     * Draw a piece of text on this panel
     *
     * @param g graphics for this panel
     * @param s the text to draw
     * @param xoffset offset text by this amount in x-direction
     */
    private void renderString(Graphics g, String s, int xoffset)
    {
	if (g==null) 
	    g = getGraphics();

	yoffset += font_height;       	
	g.drawString(s,xoffset,yoffset);
	ig.drawString(s,xoffset,yoffset);
    }

    /**
     * Draw a "dot" (a node) on this panel
     *
     * @param dpy graphics for this panel
     * @param x x-coord of center
     * @param y y-coord of center
     * @param diameter diameter of dot
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
     * Add entry to panel. The entry is rendered immediately and it thus
     * not explicitly stored.
     *
     * @param color the color of the entry
     * @param s the description of the entry
     */
    public void addLegend(int color, String s)
    {
	Graphics g = getGraphics();

	g.setFont(f);
	setRenderColor(g,getColor(color));

	renderDot(g,10,yoffset+font_height-5,10);

	setRenderColor(g,Color.black);
	renderString(g,s,20);
    }

    /**
     * Erase all
     */
    public void clearLegend()
    {
	Graphics g = getGraphics();

	g.setFont(f);
	clearToBack();

	setRenderColor(g,Color.black);
	yoffset = 1;
	renderString(g,"Legend",5);
    }

}
