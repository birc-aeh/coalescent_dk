
import java.awt.*;

/**
 * The card holding the animation and animation control
 *
 * @author Anders Mikkelsen 
 */
public class AnimationCard extends Panel
{

    /// The animation
    private Animation a;

    /// The animation control
    private Control c;

    /// The width of the card
    int width;
    
    /// The height of the card
    int height;

    /**
     * Construct a new card with a given width and height
     *
     * @param width   the desired width
     * @param height  the desired height
     */
    public AnimationCard(int width, int height)
    {
	setLayout(new BorderLayout());
	
	String s[] = {"Speed","Rewind","Pause","Play","Step","FForward"};
	c = new Control(s);
	add(c,"South");

	Dimension d = c.getPreferredSize();
	a = new Animation(width,height-d.height);
	add(a,"Center");
	
	c.setAnimationListener(a);
	a.setControlListener(c);
	
	this.width = width;
	this.height = height;
    }

    
    /**
     * Set an InfoListener for this card
     *
     * @param i  the infolistener
     */
    public void setInfoListener(InfoListener i) { a.setInfoListener(i); }

    /**
     * Get an animation listener from this card
     */
    public AnimationListener getAnimationListener() { return a; }

    /**
     * Let the preferred size of the card equal the size given in the constructor
     */
    public Dimension getPreferredSize()
    {
	return new Dimension(width,height);
    }

}
