
import java.awt.*;

/** 
 * Simple textual status bar
 * 
 * @author Anders Mikkelsen
 */
public class StatusBar extends Panel
{
    /** 
     * The label holding the status message
     * 
     */
    static Label lStatus;

    /** 
     * Intro thread
     * 
     */
    static Intro intro = null;


    /** 
     * Make a new status bar
     * 
     */
    public StatusBar()
    {
	GridBagConstraints gbc = new GridBagConstraints();

	lStatus = new Label("Status");
	lStatus.setBackground(GUIConfig.buttonbgcolor);

	setLayout(new GridBagLayout());
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.weightx = 1.0;
	add(lStatus, gbc);	       
	intro = new Intro(this);
    }

    /** 
     * Stop intro and change status text
     * 
     * @param msg the new statuc message
     */
    static void setStatus(String msg)
    {
	stopIntro();
	lStatus.setText(msg);
    }

    /** 
     * Start the intro
     * 
     */
    static void startIntro()
    { 
	if (intro!=null) intro.start();
    }

    /** 
     * Stop the intro
     * 
     */
    static void stopIntro() 
    { 
	if (intro!=null) {
	    intro.halt();
	    intro = null;
	}
    }

    /** 
     * Set status message
     * 
     * @param msg 
     */
    static void setMessage(String msg)
    {
	lStatus.setText(msg);
    }

}
