
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.server.*;

/**
 * The top-level GUI
 *
 * @author Anders Mikkelsen
 */
public class GUI extends Panel
{
    /// The main animation and extracted trees
    AnimationCards a;

    /// Information window
    InfoWindow iw;

    /// Statusbar
    StatusBar sb;

    /// Model parameters
    ParameterPanel pp;

    /// Legends
    Legends le;

    /// Borders
    BorderedPanel bpp, biw, ble;

    /// Layout
    Panel cw, all, right;

    /// Keyboard shortcuts
    KeyListener kListener;

    /// All animation listeners
    Vector aListeners;
    

    /**
     * New GUI with indicated dimensions OF THE MAIN ANIMATION CARDS!!. The
     * ppanel argument gives the name of the class to initially load into
     * the model parameter slot.
     *
     * @param width width of the animation cards
     * @param height of the animation cards
     * @param ppanel name of initial model parameter panel
     */
    public GUI(int width, int height, int ppanel)
    {
	setBackground(GUIConfig.bgcolor);

	kListener = new smallKeyListener();
	addContainerListener(new smallContainerListener());

	all = new Panel();
	all.setLayout(new BorderLayout());

	sb = new StatusBar();
	all.add(sb,"South");

	cw = new Panel();
	a = new AnimationCards(width, height);
	aListeners = a.getAnimationListeners();
	cw.add(a);

	le = new Legends(200,120);
	ble = new BorderedPanel(le);

	iw = new InfoWindow(200,100);
	biw = new BorderedPanel(iw);

	pp = new ParameterPanel(ppanel);
	pp.setBackground(GUIConfig.parbgcolor);
	bpp = new BorderedPanel(pp);

	right = new Panel();
	right.setLayout(new GridBagLayout());
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.fill = GridBagConstraints.HORIZONTAL;

	gbc.gridy = 0;
	right.add(ble,gbc);
	gbc.gridy++;
	gbc.insets = new Insets(15,0,15,0);
	right.add(bpp,gbc);	
	gbc.gridy++;
	gbc.insets = new Insets(0,0,0,0);
	right.add(biw,gbc);
	cw.add(right);

  	all.add(cw,"Center");

	setLayout(new GridBagLayout());
	add(all);

	VectorIterator vi = new VectorIterator(a.getAnimationListeners());
	while (vi.hasNext()) {
	    pp.addAnimationListener((AnimationListener)(vi.next()));
	}
	pp.setLegendListener(le);

	a.setInfoListener(iw);

	StatusBar.startIntro();

    }


    /**
     * Magic mumbo required to get global keyboard shortcuts
     */
    private class smallContainerListener extends ContainerAdapter
    {
	public void componentAdded(ContainerEvent e)
	{
	    // Listeners are removed before they are added
	    // to prevent adding the same listener multiple times
	    Component c = e.getChild();
	    c.addKeyListener(kListener);
	    if (c instanceof Container) {		
		Container o = (Container)c;
		o.removeContainerListener(this);
		o.addContainerListener(this);
		addListeners(o.getComponents());
	    }
	}

	private void addListeners(Component c[])
	{
	    int i;

	    // Listeners are removed before they are added
	    // to prevent adding the same listener multiple times
	    for (i=0; i<c.length; i++) {
		c[i].removeKeyListener(kListener);
		c[i].addKeyListener(kListener);
		if (c[i] instanceof Container) {
		    Container o = (Container)c[i];
		    o.removeContainerListener(this);
		    o.addContainerListener(this);
		    addListeners(o.getComponents());
		}
	    }
	}
    }


    /**
     * Handler of the global keyboard shortcuts
     */
    private class smallKeyListener extends KeyAdapter
    {
	private void dispatchKeyPress(String s)
	{
	    VectorIterator vi = new VectorIterator(aListeners);
	    while (vi.hasNext()) {
		AnimationListener a = (AnimationListener)(vi.next());

		while (true) {
		    if (s.equals("Play")) {
			a.animationStart();
			break;
		    }
		    if (s.equals("Pause")) {
			a.animationStop();
			break;
		    }
		    if (s.equals("Rewind")) {
			a.animationRewind();
			break;
		    }
		    if (s.equals("FForward")) {
			a.animationFlush();
			break;
		    }
		    if (s.equals("Next")) {
			a.animationStep(true);
			break;
		    }
		    if (s.equals("Prev")) {
			a.animationStep(false);
			break;
		    }
		    break;
		}	
	    }
	}


	public void keyPressed(KeyEvent e)
	{
	    long mods = e.getModifiers();
	    int c = e.getKeyCode();
	    boolean consume = true;

	    if ((mods & InputEvent.CTRL_MASK)!=0 && c=='Q')
		System.exit(0);

	    switch (c) {
	    case 'P':
		dispatchKeyPress("Play");
		break;
	    case 'S':
		dispatchKeyPress("Pause");
		break;
	    case 'R':
		dispatchKeyPress("Rewind");
		break;
	    case 'F':
		dispatchKeyPress("FForward");
		break;
	    case 'N':
		dispatchKeyPress("Next");
		break;
	    case 'B':
		dispatchKeyPress("Prev");
		break;	       
	    default:
		consume = false;
	    }
		
	    if (consume) e.consume();

	    //System.out.println(e);
	}
    }

}
