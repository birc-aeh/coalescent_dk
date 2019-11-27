
import java.awt.*;

/**
 * A one child panel with border
 *
 * @author Anders Mikkelsen
 */
public class BorderedPanel extends Panel
{
    /// The child panel
    Panel thePanel;
    
    /// Shades to make the border "pretty"
    protected Color bright,dark;

    /// Offset border by this amount
    static int xoff = 1;

    /// Offset border by this amount
    static int yoff = 1;

    /// Inset inner panel by this amount
    static int inner = 1;

    
    /**
     * Make new one-child panel with border
     *
     * @param c the child
     */
    public BorderedPanel(Component c)
    {
	GridBagConstraints gbc = new GridBagConstraints();
	Color border_color = GUIConfig.buttonbgcolor;

	bright = border_color.brighter();
	dark   = border_color.darker();

	setLayout(new GridBagLayout());
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.weightx = 1.0;
	gbc.insets = new Insets(xoff+2+inner,yoff+2+inner,xoff+2+inner,yoff+2+inner);
	super.add(c, gbc);

    }

    /// Overwrite add methods to avoid adding more than one child
    public Component add() { return null; }

    /// Overwrite add methods to avoid adding more than one child
    public Component add(Component c) { return null; }

    /// Overwrite add methods to avoid adding more than one child
    public Component add(String name, Component c) { return null; }

    /// Overwrite add methods to avoid adding more than one child
    public Component add(Component c, int i) { return null; }

    /// Overwrite add methods to avoid adding more than one child
    public void add(Component c, Object o) { return; }

    /// Overwrite add methods to avoid adding more than one child
    public void add(Component c, Object o, int i) { return; }

    /**
     * Paint the neat border
     */
    public void paint(Graphics g)
    {
	super.paint(g);
	int x1,y1,x2,y2;

	x1 = xoff;
	y1 = yoff;

	x2 = getSize().width - 1 - xoff;
	y2 = getSize().height - 1 - yoff;

	g.setColor(bright);
	g.drawLine(x1,y1,x2,y1);
	g.drawLine(x1,y1,x1,y2);
	g.drawLine(x2-1,y1+1,x2-1,y2-1);
	g.drawLine(x1+1,y2-1,x2-1,y2-1);

	g.setColor(dark);
	g.drawLine(x1+1,y1+1,x2-1,y1+1);
	g.drawLine(x1+1,y1+1,x1+1,y2-1);
	g.drawLine(x2,y1,x2,y2);
	g.drawLine(x1,y2,x2,y2);
    }

}
