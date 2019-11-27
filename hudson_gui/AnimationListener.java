
/**
 * Implemented by classes capable of doing animation using
 * AnimationTrees. The basic idea is like that of a VCR:<p>
 * <ul>
 * <li> Load the "movie" (animation) into the "machine"
 * <li> Run it, Pause it, Rewind it, etc.
 * </ul>
 *
 * @author Anders Mikkelsen
 * @see AnimationTree
 */
public interface AnimationListener {
    
    /**
     * Prepare animator for visualization - like the "on" button on the VCR
     */
    public void animationPrepare();

    /**
     * Load and start a new animation
     *
     * @param t the new AnimationTree
     */
    public void animationNew(AnimationTree t);

    /**
     * Start/Restart current animation
     */
    public void animationStart();

    /**
     * Stop current animation
     */
    public void animationStop();

    /**
     * Rewind current animation
     */
    public void animationRewind();

    /**
     * Roll animation one "frame" forwards
     */
    public boolean animationStep(boolean forward);

    /**
     * Roll animation to the end
     */ 
    public void animationFlush();

    /**
     * Set animation speed
     *
     * @param s speed in range 0 to 100
     */
    public void animationSpeed(int s);

}
