
import java.awt.*;

/** 
 * Panel holding the main animation canvas and the control panel
 * 
 * @author Anders Mikkelsen
 */
public class EvolveCard extends Panel
{
    /** 
     * The main animation canvas
     * 
     */
    Evolver ev;
    /** 
     * The control panel
     * 
     */
    Control ctl;

    /** 
     * Create a new panel. Note that the indicated dimensions are dimensions
     * of the animation canvas, not the dimensions of this panel.
     * 
     * @param width desired width of the main animation canvas
     * @param height desired height of the main animation canvas
     */
    public EvolveCard(int width, int height)
    {
	super();

	ev = new Evolver(width,height);

	String buttons[] = {"Speed","UnTangle","Rewind","Pause","Play","Step","FForward"};
	ctl = new Control(buttons);

	setLayout(new BorderLayout());
	add(ev,"Center");
	add(ctl,"South");

	ctl.setModelListener(ev);
	ev.setControlListener(ctl);
    }

    /** 
     * Get a model listener from this card
     * 
     * @return a model listener from this card (there is only one - the main 
     *     animation canvas)
     */
    public ModelListener getModelListener() { return ev; }

}
