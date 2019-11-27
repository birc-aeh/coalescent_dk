
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

/**
 * Container for the current model parameter panel. It handles the dynamic
 * loading of a new model parameter panel on request from the user.
 *
 * @author Anders Mikkelsen
 */
public class ParameterPanel extends Panel implements ActionListener, ItemListener
{
    /// Recalculate button
    Button bRecalc;

    /// Available panels
    Choice cPanels;

    /// Layout
    Panel all;
    Panel thePP;

    /// Current model parameter panel
    ModelParameters mPar;

    /// The class name of the current model parameter panel
    private String last_parameters;

    /// Animation listeners listening on this panel
    private Vector aListeners;

    /// Legend listeners listening on this panel
    private LegendListener lListener;
    

    /**
     * Load a new model parameter panel
     *
     * @param s class name of panel to load
     */
    public void loadParameters(String s)
    {
	thePP.removeAll();

	mPar = null;
	try {
	    ClassLoader cl = getClass().getClassLoader();
	    mPar = (ModelParameters)(cl.loadClass(s).newInstance());
	} catch (Exception e) { e.printStackTrace(); }

	if (mPar != null) {
	    thePP.add(mPar);
	    last_parameters = s;
	} else {
	    Label l = new Label("ClassLoad Error");
	    l.setForeground(GUIConfig.labelcolor);
	    thePP.add(l);
	}

	validate();
    }


    public void changeParameters(int i)
    {
	thePP.removeAll();
	switch (i) {
	case 0:
	    mPar = new RecombParam();
	    break;
	case 1:
	    mPar = new MigrateParam();
	    break;
	case 2:
	    mPar = new ExprateParam();
	    break;
	case 3:
	    mPar = new SelectionParam();
	    break;
	default:
	    mPar = new RecombParam();
	    break;
	}
	thePP.add(mPar);
	validate();
    }


    /**
     * New parameter panel
     *
     * @param s class name of panel to initially load
     */
    public ParameterPanel(int init)
    {
	aListeners = new Vector();

	all = new Panel();

	all.setLayout(new GridBagLayout());	
	GridBagConstraints gbc = new GridBagConstraints();

	cPanels = new Choice();
	cPanels.setBackground(GUIConfig.buttonbgcolor);
	cPanels.add("Recombination");
	cPanels.add("Migration");
	cPanels.add("Exp. growth");
	cPanels.add("Selection");	
	cPanels.addItemListener(this);
	gbc.gridy = 0;
	all.add(cPanels,gbc);

	thePP = new Panel();
	gbc.gridy++;
	all.add(thePP,gbc);

	cPanels.select(init);
	changeParameters(init);
	//loadParameters(s);

	bRecalc = new Button("Recalc");
	bRecalc.setBackground(GUIConfig.buttonbgcolor);
	bRecalc.setActionCommand("recalc");
	bRecalc.addActionListener(this);

	gbc.gridy++;
	gbc.insets = new Insets(10,0,0,0);
	all.add(bRecalc,gbc);
	if (mPar == null) bRecalc.setEnabled(false);

	gbc.insets = new Insets(35,0,35,0);
	setLayout(new GridBagLayout());
	add(all,gbc);
	
	addMouseListener(new smallMouseListener());
    }

    /**
     * Add animation listener
     *
     * @param a the animation listener to add
     */
    public void addAnimationListener(AnimationListener a)
    {
	aListeners.addElement(a);
    }

    /**
     * Set the legend listener (only one can listen)
     *
     * @param l the legend listener
     */
    public void setLegendListener(LegendListener l)
    {
	lListener = l;
    }


    /**
     * React on the user clicking the recalc button. This involves invoking the
     * CGI-script back on the server.
     */
    public void actionPerformed(ActionEvent ae)
    {
	String command = ae.getActionCommand();
	VectorIterator vi;
	
	if (command.equals("recalc")) {
	    if (aListeners.size()==0 || mPar==null) return;

  	    try {
		StatusBar.setStatus("Connecting to server...");		
		BufferedReader br = CGIConnect.invoke(mPar.cgiCommand());

		// DEBUG START
		// hos jotun og carsten
		// kaster exception fra animationtree-constructoren

		System.out.println("\n---\n");

		System.out.println("Debug reference: version 1 - Lasse Westh-Nielsen 24/9/2002");

		// print cgi requestet
		System.out.println("Kommando sendt: " + mPar.cgiCommand());

		// print cgi svaret

		System.out.println("Waiting for stream...");

		while (! br.ready()) { }

		System.out.println("Stream ready!");

		Vector v = new Vector();
		
		String temp = new String();

		while ((temp = br.readLine()) != null)
		    {
			v.addElement(temp);
		    }
		
		// DEBUG SLUT
		
		br.close();
		
		System.out.println("Response from CGI request:");

		for (int i=0; i<v.size(); i++)
		    {
			System.out.println((String) v.elementAt(i));
		    }
		
		System.out.println("End of response");

		StatusBar.setStatus("Reading tree...");
		AnimationTree at = new AnimationTree(v,lListener);

		vi = new VectorIterator(aListeners);
		while (vi.hasNext()) {
		    AnimationListener a = (AnimationListener)(vi.next());
		    a.animationNew(at);
		    a.animationStart();
		}
		StatusBar.setStatus("Animation ready and running");
	    } catch (Exception e) {
		e.printStackTrace();
		StatusBar.setStatus("An error occurred during tree download");
	    }
	    return;
	}
    }

    /**
     * Undocumented feature: CTRL + Mouse-1 opens a dialog to select a
     * new parameter panel while the GUI is running. This is handled by
     * this listener.
     */
    private class smallMouseListener extends MouseAdapter implements ActionListener
    {
	/// Is the selection dialog visible
	private boolean selectShowing;

	public smallMouseListener() { selectShowing = false; }

	/**
	 * Open the select dialog
	 */
	public void mouseClicked(MouseEvent e)
	{
	    int m = e.getModifiers();

	    if ((m & InputEvent.CTRL_MASK)!=0    && 
		(m & InputEvent.BUTTON1_MASK)!=0 &&
		(!selectShowing)) {
		thePP.removeAll();
		thePP.add(new ParameterSelectDialog(this));
		validate();
	    }
	}

	/**
	 * Possibly load a new model parameter panel
	 */
	public void actionPerformed(ActionEvent ae)
	{
	    String command = ae.getActionCommand();

	    if (command != null)
		loadParameters(command);
	    else
		loadParameters(last_parameters);
	}
    }


    public void itemStateChanged(ItemEvent iev)
    {
	Choice c = (Choice)iev.getItemSelectable();
	//System.out.println(c.getSelectedIndex());
	changeParameters(c.getSelectedIndex());
    }
    
}
