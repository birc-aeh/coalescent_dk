
import java.awt.*;
import java.awt.event.*;


public class main extends Frame
{
    private class smallWindowListener extends WindowAdapter
    {
	public void windowClosing(WindowEvent e)
        {
            System.exit(0);
        }
    }


    public main()
    {
  	GUI g = new GUI(400,500,0);
  	add(g);

	pack();
	addWindowListener(new smallWindowListener());
	setTitle("Hudson Animator");
	show();

    }

    
    public static void main(String argv[])
    {
	main m = new main();

	try {
	    CGIConnect.setScript("https://coalescent.dk/cgi-bin/simulate");
	} catch (Exception e) {
	    System.err.println(e);
	    System.exit(1);
	}	
    }
}
