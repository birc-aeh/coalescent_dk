
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/** 
 * The control panel
 * 
 * @author Anders Mikkelsen
 */
public class Control extends Panel implements ActionListener, ControlListener, SpinListener
{
    /** 
     * Animation speed spin button
     * 
     */
    SpinButton sb;

    /** 
     * The icon buttons
     * 
     */
    Vector buttons;

    /** 
     * The untangle icon button - this button is not in the button vector
     * since its enabling/disabling is different from the other buttons
     * 
     */
    IconButton theUntangle;

    /** 
     * Model listener listening on this control panel
     * 
     */
    ModelListener mListener = null;
    
    /** 
     * Make a button
     * 
     * @param s button label
     * @return the new button
     */
    private Button makeButton(String s)
    {
	Button b = new Button(s);
	b.setBackground(GUIConfig.buttonbgcolor);
	return b;
    }

    /** 
     * Make a new label
     * 
     * @param s the label text
     * @return the new label
     */
    private Label makeLabel(String s)
    {
	Label l = new Label(s);
	l.setForeground(GUIConfig.labelcolor);
	return l;
    }

    /** 
     * Construct a new control panel with the button given in the string array
     * argument. Currently only "Speed", "Play", "Pause", "Rewind", "FForward",
     * "Step", "BackStep" and "UnTangle" are recognized
     * 
     * @param button the array of desired buttons (in order of appearance)
     */
    public Control(String button[])
    {
	int i;
	IconButton ib = null;

	Panel spanel = new Panel();
	spanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,2));

	Panel bpanel = new Panel();
	bpanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

	setLayout(new BorderLayout());
	add(spanel,"West");
	add(bpanel,"East");

	buttons = new Vector();
	for (i=0; i<button.length; i++) {
	    ib = null;
	    while (true) {
		if (button[i].equals("Speed")) {
		    Label l = new Label("Speed:",Label.RIGHT);
		    l.setForeground(GUIConfig.labelcolor);
		    sb = new SpinButton(4,50,0,100,10,true);
		    sb.setSpinListener("speed",this);
		    sb.setPost('%');
		    spanel.add(l);
		    spanel.add(sb);
		    break;
		}
		if (button[i].equals("Play")) {
		    ib = new IconButton(IconButton.Play);
		    ib.setStatusMessage("Start");
		    break;
		}
		if (button[i].equals("Pause")) {
		    ib = new IconButton(IconButton.Pause);
		    ib.setStatusMessage("Pause");
		    break;
		}
		if (button[i].equals("Rewind")) {
		    ib = new IconButton(IconButton.Rewind);
		    ib.setStatusMessage("Rewind");
		    break;
		}
		if (button[i].equals("FForward")) {
		    ib = new IconButton(IconButton.FForward);
		    ib.setStatusMessage("Fast Forward");
		    break;

		}
		if (button[i].equals("Step")) {
		    ib = new IconButton(IconButton.Step);
		    ib.setStatusMessage("Step forward");
		    break;
		}
		if (button[i].equals("BackStep")) {
		    ib = new IconButton(IconButton.BackStep);
		    ib.setStatusMessage("Step backwards");
		    break;
		}
		if (button[i].equals("UnTangle")) {
		    ib = new IconButton(IconButton.UnTangle);
		    theUntangle = ib;
		    ib.setStatusMessage("UnTangle");
		    break;
		}		
		System.err.println("Unknown button : " + button[i]);
		System.exit(1);
	    }
	    if (ib != null) {
		ib.setActionCommand(button[i]);
		ib.setActionListener(this);
		bpanel.add(ib);
		buttons.addElement(ib);
	    }
	}
	
	setButtonState(false);
    }

    /** 
     * Set a model listener for this control panel
     * 
     * @param m 
     */
    public void setModelListener(ModelListener m)
    {
	mListener = m;
	if (sb!=null)
	    mListener.modelSpeed((int)(sb.getValue()));
    }

    /** 
     * Set the state of the button in this panel
     * 
     * @param enabled true if the buttons should be enabled, false otherwise
     */
    public void setButtonState(boolean enabled)
    {
	VectorIterator vi = new VectorIterator(buttons);
	while (vi.hasNext())
	    ((IconButton)(vi.next())).setEnabled(enabled);
    }

    /** 
     * React on changes in the speed spin button
     * 
     * @param id id of the originating spin button
     * @param v new value of the spin button
     */
    public void spinValueChanged(String id, double v)
    {
	if (mListener!=null)
	    mListener.modelSpeed((int)v);
    }

    /** 
     * React on button clicks
     * 
     * @param ae action event
     */
    public void actionPerformed(ActionEvent ae)
    {
	String command = ae.getActionCommand();

	if (mListener != null) {
	    if (command.equals("Play")) {
		mListener.modelStart();
		return;
	    }
	    if (command.equals("Pause")) {
		mListener.modelStop();
		return;
	    }
	    if (command.equals("Rewind")) {
		mListener.modelRewind();
		return;
	    }
	    if (command.equals("FForward")) {
		mListener.modelFlush();
		return;
	    }
	    if (command.equals("Step")) {
		mListener.modelStep();
		return;
	    }
	    if (command.equals("BackStep")) {
		//aListener.animationStep(false);
		return;
	    }
	    if (command.equals("UnTangle")) {
		mListener.modelUntangle();
		theUntangle.setEnabled(false);
		return;
	    }
	}

	System.out.println("Action Performed : " + ae);
    }
}
