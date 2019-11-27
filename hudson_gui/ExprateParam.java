
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

/**
 * Parameter panel for the exponential growth model
 *
 * @author Anders Mikkelsen
 */
public class ExprateParam extends ModelParameters
{
    /// Number of sequences
    SpinButton sbN;

    /// Exp-rate
    SpinButton sbR;

    /// Labels
    Label lN, lR;


    /**
     * Create new exp-rate panel
     */
    public ExprateParam()
    {
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();

	setLayout(gridbag);

	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.gridx = 0;
	gbc.gridy = 0;

	gbc.gridwidth = 1;
	gbc.gridy++;
	lN = makeLabel("n:");
	add(lN,gbc);
	
	gbc.gridx = 1;
	sbN = makeSpinButton(5,10.0,2.0,10.0,1.0,true);
	add(sbN,gbc);
	
  	gbc.gridx = 0;
  	gbc.gridy++;
  	lR = makeLabel("exp:");
  	add(lR,gbc);
	
  	gbc.gridx = 1;
  	sbR = makeSpinButton(5,1.0,0.0,10000.0,2.0,false);
	sbR.setExp(true);
  	add(sbR,gbc);

	gbc.gridy++;
  	gbc.gridx = 0;
	gbc.gridwidth = 2;
    }


    /**
     * Generate argument to the CGI-script
     *
     * @return argument to the animate CGI script
     */
    public String cgiCommand()
    {  
	StringBuffer param = new StringBuffer("command=exprate");
	param.append("&n=" + (int)(sbN.getValue()));
	param.append("&exp=" + sbR.getValue());
	return param.toString();
    }
}
