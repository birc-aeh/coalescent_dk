
import java.awt.*;


/** 
 * Class for constructing the main user interface
 * 
 * @author Anders Mikkelsen
 */
public class GUI extends Panel
{
    /** 
     * The parameters panel
     * 
     */
    Parameters p;

    /** 
     * The border around the parameters panel
     * 
     */
    BorderedPanel bp;

    /** 
     * The main animation card (animation and controls)
     * 
     */
    EvolveCard ec;

    /** 
     * Border around the main animation card
     * 
     */
    BorderedPanel bec;

    /** 
     * The status bar
     * 
     */
    StatusBar sb;

    /** 
     * The border around the status bar
     * 
     */
    BorderedPanel bsb;
    
    /** 
     * Arrange the individual panels to form a nice GUI
     * 
     * @param width desired width of the MAIN ANIMATION CANVAS
     * @param height desired height of the MAIN ANIMATION CANVAS
     */
    public GUI(int width, int height)
    {
	setBackground(GUIConfig.bgcolor);
	setLayout(new BorderLayout());

	Colors.init(2);

	ec = new EvolveCard(width,height);
	bec = new BorderedPanel(ec);
	add(bec,"Center");

	p = new Parameters();
	bp = new BorderedPanel(p);
	add(bp,"East");

	sb = new StatusBar();
	bsb = new BorderedPanel(sb);
	add(bsb,"South");
	sb.startIntro();

	p.setModelListener(ec.getModelListener());
    }




}
