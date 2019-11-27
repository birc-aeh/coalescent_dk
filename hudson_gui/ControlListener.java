
/**
 * Control panels implements this interface
 * 
 * @author Anders Mikkelsen
 */
public interface ControlListener
{
    /**
     * Set the state of the buttons in the panel implementing the interface
     *
     * @param b true if the button should be enabled
     */
    public void setButtonState(boolean b);
}

