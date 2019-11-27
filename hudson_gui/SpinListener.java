
/**
 * Implement this interface to be able to listen on spin buttons
 *
 * @author Anders Mikkelsen
 * @see SpinButton
 */
public interface SpinListener
{
    /**
     * The value of the spin button changed
     *
     * @param v the new value
     */
    public void spinValueChanged(double v);

}
