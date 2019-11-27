
import java.awt.*;

/**
 * Panel holding the extracted trees and associated controls and sliders
 *
 * @author Anders Mikkelsen
 */
public class ScannerCard extends Panel
{
    /// The control
    Control c;

    /// The extracted trees
    Animation a;

    /// The slider
    Slider sld;

    /// Dimensions
    int width,height;

    /**
     * New scanner card
     *
     * @param width desired width of card
     * @param height desired height of card
     */
    public ScannerCard(int width, int height)
    {
	setLayout(new BorderLayout());
	
	String s[] = {"Rewind","BackStep","Step","FForward"};
	c = new Control(s);
	add(c,"South");

	sld = new Slider(width);
	add(sld,"Center");

	Dimension d = c.getPreferredSize();
	Dimension d2 = sld.getPreferredSize();
	a = new Animation(width,height - d.height - d2.height);
	add(a,"North");
	
	c.setAnimationListener(sld);
	sld.setControlListener(c);
	sld.setAnimationListener(a);

	this.width = width;
	this.height = height;
    }

    /**
     * Get all animation listeners on this card (one)
     *
     * @return the animation listener on the card
     */
    public AnimationListener getAnimationListener() { return sld; }

    /**
     * Set an info listener for this card
     * 
     * @param i the info listener
     */
    public void setInfoListener(InfoListener i)
    {
	if (a != null)
	    a.setInfoListener(i);
    }


    /**
     * Make correct preferred size
     */
    public Dimension getPreferredSize()
    {
	return new Dimension(width,height);
    }

}
