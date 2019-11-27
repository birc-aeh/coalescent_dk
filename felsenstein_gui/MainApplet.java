
import java.applet.*;
import java.net.*;
import java.awt.*;
import java.util.*;

public class MainApplet extends Applet
{
    public void init()
    {
	int w,h;

	setBackground(Color.white);

	w = 300;
	h = 400;
	try {
	    w = Integer.parseInt(getParameter("iwidth"));
	    h = Integer.parseInt(getParameter("iheight"));
	} catch (Exception e) {};

	GUI g = new GUI(w,h);
  	add(g);
    }



    public void start()
    {
//  	URL codebase = getCodeBase();
//  	String path = getParameter("cgipath");
	
//  	try {
//  	    CGIConnect.setScript("http://" + codebase.getHost() + path);	    
//  	} catch (Exception e) {
//  	    System.out.println(e);
//  	    System.exit(1);
//  	}
    }
    
}
