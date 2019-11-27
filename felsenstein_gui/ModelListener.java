

/** 
 * Classes that visualize models should implement this interface. Note that
 * it has got nothing to do with the Model class
 * 
 * @author Anders Mikkelsen
 */
public interface ModelListener
{

    /** 
     * The model changed
     * 
     * @param m the new model
     */
    public void modelNew(Model m);

    /** 
     * Step the model one step
     * 
     * @return false if this was the last step, true otherwise
     */
    public boolean modelStep();

    /** 
     * Stop the visualization (animation)
     * 
     */
    public void modelStop();

    /** 
     * Change animation speed
     * 
     * @param s new speed (percent)
     */
    public void modelSpeed(int s);

    /** 
     * Untangle the model
     * 
     */
    public void modelUntangle();

    /** 
     * Start the visualization (animation)
     * 
     */
    public void modelStart();

    /** 
     * Flush visualization (show all)
     * 
     */
    public void modelFlush();

    /** 
     * Wind model to start (show only first generation)
     * 
     */
    public void modelRewind();

}
