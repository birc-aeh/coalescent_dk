
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

/**
 * Parameter panel for the migration model
 *
 * @author Anders Mikkelsen
 */
public class MigrateParam extends ModelParameters
{
    // Spinbutton
    SpinButton sbN, sbM1, sbM2;

    // Label
    Label lN, lM1, lM2;

    /**
     * Make the panel
     */
    public MigrateParam()
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
  	lM1 = makeLabel("M1:");
  	add(lM1,gbc);
	
  	gbc.gridx = 1;
  	sbM1 = makeSpinButton(4,0.5,0.0,10.0,0.5,false);
  	add(sbM1,gbc);

  	gbc.gridx = 0;
  	gbc.gridy++;
  	lM2 = makeLabel("M2:");
  	add(lM2,gbc);
	
  	gbc.gridx = 1;
  	sbM2 = makeSpinButton(4,0.5,0.0,10.0,0.5,false);
  	add(sbM2,gbc);
    }

    /**
     * Construct the CGI-argument
     *
     * @return CGI-argument
     */
    public String cgiCommand()
    {  
	StringBuffer param = new StringBuffer("command=migrate");
	param.append("&n=" + (int)sbN.getValue());
	param.append("&m1=" + sbM1.getValue());
	param.append("&m2=" + sbM2.getValue());
	return param.toString();
    }



}
