
import java.awt.*;

/**
 * The intro waits for the status bar to
 * become visible and then runs an intro
 * it.
 *
 * @author Anders Mikkelsen
 */
public class Intro extends Thread
{
    private boolean stopNow;
    Component stat;
    static String messages[] = { "Hudson Animator",
				 "First enter parameters",
				 "Then press the Recalc button",
				 "Control animation with the Start, Pause and Rewind buttons" };

    /**
     * Make new animation intro in an animation panel
     *
     * @param a  the panel to make intro in
     */
    public Intro(Component stat)
    {
	this.stat = stat;
    }

    /**
     * Main loop
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
     */
    public void halt()
    {
	stopNow = true;
    }

}
