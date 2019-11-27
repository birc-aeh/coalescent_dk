
/**
 * The animation timer keeps stepping an animation until
 * the animation is done or until it is halted.
 *
 * @author Anders Mikkelsen
 * @see Animation
 */
public class AnimationTimer extends Thread
{
    /**
     * The animaton to step
     */
    AnimationListener aListener;

    /**
     * Time (msec) to sleep
     */
    private int sleepTime;


    /**
     * Stop stepping if this is set
     */
    private boolean stopNow;

    /**
     * Make new animation timer
     *
     * @param al the animation to step
     */
    public AnimationTimer(AnimationListener al, int timer_delay)
    {
	aListener = al;
	stopNow = false;
	sleepTime = timer_delay;
    }
    
    /**
     * The stepping loop. The timer exits the loop if the animation
     * is done or if stopNow is true or if this thread is interrupted
     */
    public void run() 
    {
	while (true) {
	    if (stopNow) return;
	    if (!aListener.animationStep(true)) return;
	    try {
		sleep(sleepTime);	   
	    } catch (Exception e) { return; };
	}
    }

    /**
     * Stop timer
     */
    public void halt() { stopNow = true; }

    /**
     * Set sleep time
     *
     * @param time the new sleep time (msec)
     */
    public void setTime(int time) { sleepTime = time; }
    
}
