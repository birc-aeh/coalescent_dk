
import java.awt.*;
import java.awt.event.*;

/** 
 * Parameter panel allowing the user to enter the number of
 * genes and the number of generations
 * 
 * @author Anders Mikkelsen
 */
public class Parameters extends Panel implements ActionListener, SpinListener
{
    /** 
     * The New button
     */
    private Button bNew;
    /** 
     * The N label
     */
    private Label lN;
    /** 
     * The G label
     */
    private Label lG;
    /** 
     * The N spin button
     */
    private SpinButton bN;
    /** 
     * The G spin button
     */
    private SpinButton bG;
    /** 
     * A model listener listening on this parameter panel
     */
    private ModelListener mListener;



    /** 
     * Make a new button
     * 
     * @param label the button label
     * @return a new button
     */
    private Button makeButton(String label)
    {
	Button b = new Button(label);
	b.setBackground(GUIConfig.buttonbgcolor);
	return b;
    }

    /** 
     * Make a new spin button
     * 
     * @param cols number of columns
     * @param val initial value
     * @param min minimum value
     * @param max maximum value
     * @param step step size
     * @param asInt true if the spin button should take on integer values only
     * @return the new spin button
     */
    protected SpinButton makeSpinButton(int cols, double val, double min, double max, double step, boolean asInt)
    {
        return new SpinButton(cols,val,min,max,step,asInt);
    }

    /** 
     * Make a new label
     * 
     * @param text the label text
     * @return the new label
     */
    protected Label makeLabel(String text)
    {
	Label l = new Label(text);
	l.setForeground(GUIConfig.labelcolor);
	return l;
    }

    /** 
     * Make the parameter panel
     */
    public Parameters()
    {
	setLayout(new GridBagLayout());
	GridBagConstraints gbc = new GridBagConstraints();

	gbc.insets = new Insets(1,15,1,15);
	gbc.fill = GridBagConstraints.BOTH;

	Panel p = new Panel(new FlowLayout(FlowLayout.RIGHT,0,0));	
	lN = makeLabel("N:");
	p.add(lN);
	bN = new SpinButton(4,10,0,30,1,true);
	p.add(bN);

	gbc.gridy = 0;
	add(p,gbc);

	p = new Panel(new FlowLayout(FlowLayout.RIGHT,0,0));
	lG = makeLabel("G:");
	p.add(lG);
	bG = new SpinButton(4,15,0,30,1,true);
	p.add(bG);

	gbc.gridy++;
	add(p,gbc);

	bNew = makeButton("New");
	bNew.setActionCommand("new");
	bNew.addActionListener(this);
	gbc.gridy++;
	add(bNew,gbc);

    }

    /** 
     * Set the model listener for this parameter panel
     * 
     * @param m the new model listener
     */
    public void setModelListener(ModelListener m)
    {
	mListener = m;
    }

    /** 
     * Callback - react on the user clicking the New button
     * 
     * @param ae 
     */
    public void actionPerformed(ActionEvent ae)
    {
	String command = ae.getActionCommand();
	if (mListener != null) {
	    if (command.equals("new")) {
		Model m = new WrightFisherModel((int)(bN.getValue()),(int)(bG.getValue()));
		m.evolve();
		mListener.modelNew(m);
		return;
	    }

	    if (command.equals("step")) {
		mListener.modelStep();
		return;
	    }

	    if (command.equals("stop")) {
		mListener.modelStop();
		return;
	    }

	    if (command.equals("untangle")) {
		mListener.modelUntangle();
		return;
	    }
	}
    }

    /** 
     * Callback - the value of one of the spin buttons changed
     * 
     * @param id 
     * @param v 
     */
    public void spinValueChanged(String id, double v)
    {
        if (mListener!=null) {
	    if (id.equals("speed"))
		mListener.modelSpeed((int)v);
	}
    }





}
