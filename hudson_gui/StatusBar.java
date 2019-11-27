
import java.awt.*;

/**
 * Simple status bar displaying text messages. Draws its own border
 *
 * @author Anders Mikkelsen
 */
public class StatusBar extends Panel
{
    /// Status label
    static Label lStatus;

    /// Border color
    static Color bright = GUIConfig.buttonbgcolor.brighter();
    static Color dark   = GUIConfig.buttonbgcolor.darker();

    /// Offsets and insets
    static int xoff = 1;
    static int yoff = 1;
    static int inner = 1;

    /// Thread running the intro
    static Intro intro = null;

    /**
     * Create the status bar
     */
    public StatusBar()
    {
	GridBagConstraints gbc = new GridBagConstraints();

	lStatus = new Label("Status");
	lStatus.setBackground(GUIConfig.buttonbgcolor);

	setLayout(new GridBagLayout());
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.weightx = 1.0;
	gbc.insets = new Insets(xoff+2+inner,yoff+2+inner,xoff+2+inner,yoff+2+inner);
	add(lStatus, gbc);	       
	intro = new Intro(this);
    }

    /**
     * Stop intro and set message
     *
     * @param msg the new message to display
     */
    static void setStatus(String msg)
    {
	stopIntro();
	lStatus.setText(msg);
    }

    /**
     * Start an intro sequence
     */
    static void startIntro()
    { 
	if (intro!=null) intro.start();
    }


    /**
     * Stop the intro sequence
     */
    static void stopIntro() 
    { 
	if (intro!=null) {
	    intro.halt();
	    intro = null;
	}
    }

    /**
     * Stop intro and set message
     *
     * @param msg the new message to display
     */
    static void setMessage(String msg)
    {
	lStatus.setText(msg);
    }

    /**
     * Paint border
     */
    public void paint(Graphics g)
    {
	super.paint(g);
	int x1,y1,x2,y2;

	x1 = xoff;
	y1 = yoff;

	x2 = getSize().width - 1 - xoff;
	y2 = getSize().height - 1 - yoff;

	g.setColor(bright);
	g.drawLine(x1,y1,x2,y1);
	g.drawLine(x1,y1,x1,y2);
	g.drawLine(x2-1,y1+1,x2-1,y2-1);
	g.drawLine(x1+1,y2-1,x2-1,y2-1);

	g.setColor(dark);
	g.drawLine(x1+1,y1+1,x2-1,y1+1);
	g.drawLine(x1+1,y1+1,x1+1,y2-1);
	g.drawLine(x2,y1,x2,y2);
	g.drawLine(x1,y2,x2,y2);
    }
}
