
import java.applet.*;
import java.net.*;
import java.awt.*;
import java.util.*;

/**
 * Class to instantiate the animation as an applet
 *
 * @author Anders Mikkelsen
 */
public class AppletAnimation extends Applet
{

    /**
     * Read parameters and instantiate the GUI
     */
    public void init()
    {
	int ix;
	setBackground(Color.white);

	String param = getParameter("parameter_panel");

	ix = 0;
	while (true) {
	    if (param.equals("RecombParam")) {
		ix = 0;
		break;
	    }
	    if (param.equals("MigrateParam")) {
		ix = 1;
		break;
	    }
	    if (param.equals("ExprateParam")) {
		ix = 2;
		break;
	    }
	    if (param.equals("SelectionParam")) {
		ix = 3;
		break;
	    }
	    break;
	}

  	GUI g = new GUI(450,500,ix);
  	add(g);
    }


    /**
     * Initialize the CGI connection back to the originating host
     */
    public void start()
    {
	URL codebase = getCodeBase();
	String path = getParameter("cgipath");
	
	try {
	    CGIConnect.setScript("http://" + codebase.getHost() + path);	    
	} catch (Exception e) {
	    System.out.println(e);
	    System.exit(1);
	}
    }
    
}
