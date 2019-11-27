
/** 
 * A small class spawning of a new thred generating impulses until
 * it is interrupted or halted
 * 
 * @author Anders Mikkelsen
 * @see ImpulseListener
 */
public class ImpulseGenerator extends Thread
{
    /** 
     * The impulse listener listening on this impulse generator
     * 
     */
    ImpulseListener iListener;

    /** 
     * Time (msec) between impulses
     * 
     */
    private int sleepTime;


    /** 
     * Stop if this is set
     * 
     */
    private boolean stopNow;

    /** 
     * Make a new impulse generator
     * 
     * @param il the impulse listener listening on this impulse generator
     * @param timer_delay delay (msec) between impulses
     */
    public ImpulseGenerator(ImpulseListener il, int timer_delay)
    {
	iListener = il;
	stopNow = false;
	sleepTime = timer_delay;
    }
    
    /** 
     * The impulse loop. The timer exits the loop if the impulse listeners
     * consumeImpulse returns false or if stopNow is true or if this thread
     * is interrupted (NOTE: the interrupting mechanism is NOT implemented
     * on a number of virtual machines, including the VM of several netscape
     * versions)
     * 
     */
    public void run() 
    {
	while (true) {
	    if (stopNow) return;
	    if (!iListener.consumeImpulse()) return;
	    try {
		sleep(sleepTime);	   
	    } catch (Exception e) { return; };
	}
    }

    /** 
     * Stop timer
     * 
     */
    public void halt() { stopNow = true; }

    /** 
     * Set time between impulses
     * 
     * @param time new time between impulses (msec)
     */
    public void setTime(int time) { sleepTime = time; }
    
}
