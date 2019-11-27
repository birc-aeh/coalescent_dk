

/** 
 * Implement this interface to listen on spin buttons
 * 
 * @author Anders Mikkelsen
 * @see SpinButton
 */
public interface SpinListener
{
    /** 
     * The value of the spin button changed
     * 
     * @param id identification of the button
     * @param v the new value
     */
    public void spinValueChanged(String id, double v);
}
