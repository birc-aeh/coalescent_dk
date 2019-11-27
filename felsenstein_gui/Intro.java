
import java.awt.*;

/** 
 * The Intro waits for a component to become visible and then
 * runs a small intro in the status bar.
 * 
 * @author Anders Mikkelsen
 */
public class Intro extends Thread
{
    /** 
     * Set to true if this intro should stop now (ie. before the next
     * iteration)
     * 
     */
    private boolean stopNow;

    /** 
     * The component to wait for visibility of
     * 
     */
    Component stat;

    /** 
     * Array of message displayed in the status bar
     * 
     */
    static String messages[] = { 
	"First enter parameters",
	"Then press the New button",
	"Control animation with the control buttons"
    };

    /** 
     * Start a new intro
     * 
     * @param stat the intro starts when this component becomes visible
     */
    public Intro(Component stat)
    {
	this.stat = stat;
    }

    /** 
     * Main loop
     * 
     */
    public void run()
    {
	int i;

	while (!stat.isShowing()) {
	    if (stopNow) return;
	    try { sleep(1000); } catch (Exception e) { return; }
	}


	i = 0;
	while (true) {
	    if (stopNow) return;
	    StatusBar.setMessage(messages[i]);
	    i = (i+1) % messages.length;
	    try { sleep(5000); } catch (Exception e) { return; }
	}
    }

    /** 
     * Stop intro
     * 
     */
    public void halt()
    {
	stopNow = true;
    }

}
