

/** 
 * Controls should implement this interface
 * 
 * @author Anders Mikkelsen
 */
public interface ControlListener
{
    /** 
     * This method is called when the state of buttons in a control should
     * change
     * 
     * @param b true if the button should be enabled, false otherwise
     */
    public void setButtonState(boolean b);
}

