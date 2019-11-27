
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

/**
 * Model parameters panel for selection
 *
 * @author Anders Mikkelsen
 */
public class SelectionParam extends ModelParameters
{
    /// Spins
    SpinButton sbN, sbR, sbU;

    /// Labels
    Label lN, lR, lU;

    /**
     * New selection panel
     */
    public SelectionParam()
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
	sbN = makeSpinButton(4,10.0,2.0,20.0,1.0,true);
	add(sbN,gbc);
	
  	gbc.gridx = 0;
  	gbc.gridy++;
  	lR = makeLabel("S:");
  	add(lR,gbc);
	
  	gbc.gridx = 1;
  	sbR = makeSpinButton(4,1.0,0.0,5.0,0.5,false);
  	add(sbR,gbc);

  	gbc.gridx = 0;
  	gbc.gridy++;
  	lU = makeLabel("U:");
  	add(lU,gbc);
	
  	gbc.gridx = 1;
  	sbU = makeSpinButton(4,3.0,0.0,20.0,1.0,false);
  	add(sbU,gbc);
    }


    /**
     * Construct CGI-argument
     */
    public String cgiCommand()
    {  
	StringBuffer param = new StringBuffer("command=selection");
	param.append("&n=" + (int)(sbN.getValue()));
	param.append("&s=" + sbR.getValue());
	param.append("&m=" + sbU.getValue());
	return param.toString();
    }



}
