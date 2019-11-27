
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
  	GUI g = new GUI(600,800);
  	add(g);

	pack();
	addWindowListener(new smallWindowListener());
	setTitle("Coalescent Theory");
	show();
    }

    
    public static void main(String argv[])
    {

	main m = new main();

    }
}
