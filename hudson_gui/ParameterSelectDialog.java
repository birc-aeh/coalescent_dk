
import java.awt.*;
import java.awt.event.*;

/**
 * There is an undocumented feature that allows for the interactive loading
 * of a new model parameter panel. The selection of a new panel is handled
 * by this small panel.
 * 
 * @author Anders Mikkelsen
 */
public class ParameterSelectDialog extends Panel implements ActionListener
{
    /// Action listener to react on an new panel being selected
    ActionListener aListener;

    /// List of panels
    List lst;

    /**
     * New model parameter selection dialog
     *
     * @param al the action listener to react on a new panel being selected
     */
    public ParameterSelectDialog(ActionListener al)
    {
	aListener = al;

	setLayout(new BorderLayout());
	setBackground(Color.gray);

	lst = new List(4, false);
	lst.setBackground(Color.white);
	lst.add("RecombParam");
	lst.add("MigrateParam");
	lst.add("ExprateParam");
	lst.add("SelectionParam");
	lst.select(0);
	add(lst,"North");

	Panel buttons = new Panel();

	Button b = new Button("Attach");
	b.setActionCommand("attach");
	b.addActionListener(this);
	buttons.add(b);

	b = new Button("Cancel");
	b.setActionCommand("cancel");
	b.addActionListener(this);
	buttons.add(b);

	add(buttons,"Center");

	setVisible(true);
    }

    /**
     * Trigger action listener
     */
    public void actionPerformed(ActionEvent ae)
    {
	String command = ae.getActionCommand();

	if (command.equals("attach")) {
	    ae = new ActionEvent(this,
				 ActionEvent.ACTION_PERFORMED,
				 lst.getSelectedItem());
	} else {
	    ae = new ActionEvent(this,
				 ActionEvent.ACTION_PERFORMED,
				 null);
	}

	aListener.actionPerformed(ae);
    }
}
