
import java.awt.*;

/**
 * Abstract class for all parameter panels. This class has methods for generating
 * labels and buttons such that all parameter panels have the same look and feel.
 *
 * @author Anders Mikkelsen
 */
public abstract class ModelParameters extends Panel
{
    /**
     * Make a label
     */
    protected Label makeLabel(String s)
    {
	Label l = new Label(s);
	l.setForeground(GUIConfig.labelcolor);
	return l;
    }

    /**
     * Make a button
     */
    protected Button makeButton(String s)
    {
	Button b = new Button(s);
	b.setBackground(GUIConfig.buttonbgcolor);
	return b;
    }

    /**
     * Make a spin button
     */
    protected SpinButton makeSpinButton(int cols, double val, double min, double max, double step, boolean asInt)
    {
	return new SpinButton(cols,val,min,max,step,asInt);
    }

    /**
     * Sub-classes must override this. The intention is to return an 
     * argument to the CGI-script making the trees back on the server.
     */
    public abstract String cgiCommand();

}
