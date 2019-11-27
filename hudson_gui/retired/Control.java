
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Control extends Panel implements ActionListener
{
    Button bStart = null;
    Button bPause = null;
    Button bRewind = null;
    Button bRecalc = null;

    SpinButton sbN = null;
    SpinButton sbR = null;
    Label lN = null;
    Label lR = null;

    AnimationListener aListener = null;
    
    private Button makeButton(String s)
    {
	Button b = new Button(s);
	b.setBackground(GUIConfig.buttonbgcolor);
	return b;
    }

    private Label makeLabel(String s)
    {
	Label l = new Label(s);
	l.setForeground(GUIConfig.labelcolor);
	return l;
    }

    public Control()
    {
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();

	setFont(new Font("Helvetica", Font.PLAIN, 12));
	setLayout(gridbag);

	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.gridwidth = 2;
	gbc.gridx = 0;
	gbc.gridy = 0;

	//bStart = makeButton("Start");
	bStart = new IconButton(IconButton.Play);
	bStart.setActionCommand("start");
	bStart.addActionListener(this);
	add(bStart,gbc);

	gbc.gridy++;
	//bPause = makeButton("Pause");
	bPause = new IconButton(IconButton.Pause);
	bPause.setActionCommand("pause");
	bPause.addActionListener(this);
	add(bPause,gbc);

	gbc.gridy++;
	//bRewind = makeButton("Rewind");
	bRewind = new IconButton(IconButton.Rewind);
	bRewind.setActionCommand("rewind");
	bRewind.addActionListener(this);
	add(bRewind,gbc);

	gbc.gridy++;
	bRecalc = makeButton("Recalc");
	bRecalc.setActionCommand("recalc");
	bRecalc.addActionListener(this);
	add(bRecalc,gbc);

	gbc.gridwidth = 1;
	gbc.gridy++;
	lN = makeLabel("n:");
	add(lN,gbc);

	gbc.gridx = 1;
	sbN = new SpinButton(3,5,2,10);
	add(sbN,gbc);
	
	gbc.gridx = 0;
	gbc.gridy++;
	lR = makeLabel("rho:");
	add(lR,gbc);

	gbc.gridx = 1;
	sbR = new SpinButton(3,1,2,10);
	add(sbR,gbc);

	setButtonState(false);
    }

    public void setAnimationListener(AnimationListener a)
    {
	aListener = a;
    }

    private void setButtonState(boolean enabled)
    {
	enabled = true;
	bStart.setEnabled(enabled);
	bPause.setEnabled(enabled);
	bRewind.setEnabled(enabled);
    }

    public void actionPerformed(ActionEvent ae)
    {
	String command = ae.getActionCommand();
	if (command == "start") {
	    aListener.animationStart();
	    return;
	}
	if (command == "pause") {
	    aListener.animationStop();
	    return;
	}
	if (command == "rewind") {
	    aListener.animationRewind();
	    return;
	}
	if (command == "recalc") {
	    aListener.animationPrepare();
	    StringBuffer param = new StringBuffer("command=hudson");
	    param.append("&n=" + sbN.getValue());
	    param.append("&r=" + sbR.getValue());

  	    try {
		StatusBar.setStatus("Connecting to server...");		
		BufferedReader br = CGIConnect.invoke(param.toString());

		StatusBar.setStatus("Reading tree...");
		AnimationTree at = new AnimationTree(br);

		aListener.animationNew(at);
		setButtonState(true);
		StatusBar.setStatus("Animation ready and running");
	    } catch (Exception e) {
		e.printStackTrace();

		StatusBar.setStatus("An error occurred during tree download");
		setButtonState(false);
	    }
	    return;
	}

	System.out.println("Action Performed : " + ae);
    }

}
