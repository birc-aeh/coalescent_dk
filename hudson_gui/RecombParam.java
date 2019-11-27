
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

/**
 * Parameter panel for the model with recombination
 *
 * @author Anders Mikkelsen
 */
public class RecombParam extends ModelParameters
{
    /// Spin
    SpinButton sbN, sbR;

    /// Label
    Label lN, lR;

    /**
     * Make new panel
     */
    public RecombParam()
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
	sbN = makeSpinButton(3,5.0,2.0,20.0,1.0,true);
	add(sbN,gbc);
	
  	gbc.gridx = 0;
  	gbc.gridy++;
  	lR = makeLabel("rho:");
  	add(lR,gbc);
	
  	gbc.gridx = 1;
  	sbR = makeSpinButton(3,0.0,0.0,10.0,1.0,true);
  	add(sbR,gbc);

	gbc.gridy++;
  	gbc.gridx = 0;
	gbc.gridwidth = 2;
    }

    /**
     * Construct the CGI-argument
     */
    public String cgiCommand()
    {  
	StringBuffer param = new StringBuffer("command=hudson");
	param.append("&n=" + (int)(sbN.getValue()));
	param.append("&r=" + (int)(sbR.getValue()));
	return param.toString();
    }



}
