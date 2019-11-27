
import java.util.*;
import java.io.*;

/**
 * StaticAnimationTrees are texts encoded as AnimationTrees.
 * They are static in the sence that they will not animate
 * but just show the encoded text
 *
 * @author    Anders Mikkelsen
 * @see       AnimationTree
 */
public class StaticAnimationTree extends AnimationTree
{
    /**
     * Line-coded representation of message
     */
    private String coded_message;

    /**
     * Number of nodes in each dir
     */
    private int xnodes, ynodes;

    /**
     * Diameter of static animation node
     */
    static int diameter = 4;

    /**
     * Max line length
     */
    static int max_line = 10;

    /**
     * Alphabet letters coded as line segments
     */
    static String alphabet[] = {
	"0,0-0,2-1,4-2,2-2,0 0,2-2,2",             //A
	"0,0-0,4-1,4-2,3-1,2-2,1-1,0-0,0 0,2-1,2", //B
	"2,0-0,0-0,4-2,4",                         //C
	"0,0-0,4-1,4-2,3-2,1-1,0-0,0",             //D
	"2,0-0,0-0,4-2,4 0,2-1,2",                 //E
	"0,0-0,4-2,4 0,2-1,2",                     //F
	"1,2-2,2-2,0-0,0-0,4-2,4",                 //G
	"0,0-0,4 0,2-2,2 2,0-2,4",                 //H
	"0,0-2,0 1,0-1,4 0,4-2,4",                 //I
	"0,4-2,4-2,0-0,0-0,1",                     //J
	"0,0-0,4 0,2-2,0 0,2-2,4",                 //K
	"2,0-0,0-0,4",                             //L
	"0,0-0,4-1,2-2,4-2,0",                     //M
	"0,0-0,4-2,0-2,4",                         //N
	"2,0-0,0-0,4-2,4-2,0",                     //O
	"0,0-0,4-2,4-2,2-0,2",                     //P
	"2,0-0,0-0,4-2,4-2,0-1,1",                 //Q
	"0,0-0,4-2,4-2,2-0,2-2,0",                 //R
	"0,0-2,0-2,2-0,2-0,4-2,4",                 //S
	"1,0-1,4 0,4-2,4",                         //T
	"0,4-0,0-2,0-2,4",                         //U
	"0,4-1,0-2,4",                             //V
	"0,4-0,0-1,1-2,0-2,4",                     //W
	"0,0-2,4 0,4-2,0",                         //X
	"0,4-1,2-1,0 1,2-2,4",                     //Y
	"0,4-2,4-0,0-2,0"                          //Z
    };


    /**
     * Makes a StaticAnimationTree from a text string
     * 
     * @param message    the string to be encoded. The string may
     *                   contain newlines and each new line may
     *                   be prefixed by an integer setting the color
     *                   of that line. Eg. "0foo\n1bar" will make
     *                   a two line message with foo in color 0 and
     *                   bar in color 1.
     */     
    public StaticAnimationTree(String message)
    {
	StringTokenizer st = new StringTokenizer(message,"\n",false);
	Vector lines = new Vector();
	int line, max, color, diff;

	// Put each message line in lines vector + find longest line
	max = 0;
	while (st.hasMoreTokens()) {
	    String s = st.nextToken();
	    lines.addElement(s);
	    max = Math.max(max,s.length());	    
	}

	// Make line-coded representation of message lines
	VectorIterator vi = new VectorIterator(lines);
	StringBuffer res = new StringBuffer();
	line = lines.size();
	while (vi.hasNext()) {
	    String s = (String)(vi.next());
	    color = 1;
	    if (Character.isDigit(s.charAt(0))) {
		color = s.charAt(0)-'0';
		s = s.substring(1);
	    }
	    diff = max-s.length();
	    s = padSpaces(s, diff/2);
	    res.append(makeTree(s,--line,color,diff%2==0));
	    if (line > 0) res.append(" ");
	}
	
	xnodes = 4*max-1;
	ynodes = 6*lines.size()-1;
	coded_message = res.toString();
    }

    /**
     * Line code single line message and offset it by y and align it
     *
     * @param message   the line to be encoded
     * @param y         the number of lines this line is offset by
     * @param color     the color of the nodes in the encoded line
     * @param aligned   if true offset line by additional half character
     *                  in the x direction
     */
    private String makeTree(String message, int y, int color, boolean aligned)
    {
	int i;
	StringBuffer msg = new StringBuffer();

	msg.append("C" + color + " ");
	for (i=0; i<message.length(); i++) {
	    if (message.charAt(i) == ' ') continue;
	    if (i>0) msg.append(" ");
	    msg.append(offsetLetter(message.charAt(i),i,y,aligned?0:2));
	}

	return msg.toString();
    }

    /**
     * Prepend spaces to a string
     *
     * @param line   the string to prepend spaces to
     * @param pad    the number of spaces to prepend
     */
    private String padSpaces(String line, int pad)
    {
	int i;
	StringBuffer sb = new StringBuffer();

	for (i=0; i<pad; i++) sb.append(" ");
	sb.append(line);

	return sb.toString();
    }

    /**
     * Recode a letter by offsetting it
     *
     * @param c      the letter to code
     * @param xoff   the number of chars to offset in x-direction
     * @param yoff   the number of chars to offset in y-direction
     * @param xextra the number of additional nodes to offset in
     *               the x-direction. This is to align lines.
     */ 
    private String offsetLetter(char c, int xoff, int yoff, int xextra)
    {
	xoff *= 4;
	xoff += xextra;
	yoff *= 6;

	if (!Character.isLetter(c)) {
	    // Raise ...
	    System.out.println("Is not letter " + c);
	    System.exit(1);	   
	}
	
	String l = alphabet[(int)(Character.toUpperCase(c)-'A')];
	StringBuffer res = new StringBuffer();


	StringTokenizer lines = new StringTokenizer(l," ",false);
	int v,comma;

	while (lines.hasMoreTokens()) {
	    StringTokenizer points = new StringTokenizer(lines.nextToken(),"-",false);
	    while (points.hasMoreTokens()) {
		String point = points.nextToken();
		for (comma=0; comma<point.length(); comma++)
		    if (point.charAt(comma) == ',') break;
		
		try {
		    v = Integer.parseInt(point.substring(0,comma));
		    res.append(v + xoff);
		    v = Integer.parseInt(point.substring(comma+1));
		    res.append(",");
		    res.append(v + yoff);
		} catch (Exception e) { System.out.println(e); System.exit(1); }

		if (points.hasMoreTokens())
		    res.append("-");
	    }
	    if (lines.hasMoreTokens()) res.append(" ");
	}

	return res.toString();
    }



    /**
     * Add edge by adding endpoints if they do not exist and
     * by adding the edge.
     * 
     * @param x1     starting x-coordinate
     * @param y1     starting y-coordinate
     * @param x2     ending x-coordinate
     * @param y2     ending y-coordinate
     * @param color  color of endpoint nodes
     */
    private void addEdge(int x1, int y1, int x2, int y2, int color)
    {
	AnimationTreeNode atn;
	int l,n1,n2;

	n1 = xnodes*y1+x1;
	n2 = xnodes*y2+x2;
	if (getNode(n2) == null) {
	    atn = new AnimationTreeNode(n2,color,(double)y2,"");
	    atn.setX(x2);
	    atn.setY(y2);
	    atn.setDiameter(diameter);
	    nodes.addElement(atn);
	}

	atn = getNode(n1);
	if (atn == null) {
	    atn = new AnimationTreeNode(n1,color,(double)y1,"");
	    atn.setX(x1);
	    atn.setY(y1);
	    atn.setDiameter(diameter);
	    nodes.addElement(atn);
	}
	atn.addEdge(n2);
    }

    /**
     * Make StaticAnimationTree based on line segments in line
     * encoded string.
     *
     * @param l  the line encoded string
     */
    private void makeNodes(String l)
    {
	StringTokenizer lines = new StringTokenizer(l," ",false);
	int comma,x1,y1,x2,y2,color;
	boolean first;
	
	x1 = y1 = x2 = y2 = 0;
	color = 1;
	while (lines.hasMoreTokens()) {
	    String line = lines.nextToken();

	    if (line.charAt(0) == 'C') {
		color = line.charAt(1)-'0';
		continue;
	    }

	    StringTokenizer points = new StringTokenizer(line,"-",false);
	    first = true;
	    while (points.hasMoreTokens()) {
		String point = points.nextToken();
		for (comma=0; comma<point.length(); comma++)
		    if (point.charAt(comma) == ',') break;
		
		try {
		    x2 = Integer.parseInt(point.substring(0,comma));
		    y2 = Integer.parseInt(point.substring(comma+1));
		} catch (Exception e) { System.out.println(e); System.exit(1); }

		if (!first) addEdge(x1,y1,x2,y2,color);
		first = false;
		x1 = x2;
		y1 = y2;
	    }
	}
    }

    /**
     * Prepare for visualization by calculating real (x,y) coordinates
     * for the tree to fit in a given area.
     *
     * @param width  the width of the visualization area
     * @param height the height of the visualization area
     */
    public int calculateCoordinates(int width, int height)
    {
	AnimationTreeNode e;
	VectorIterator vi = new VectorIterator(nodes);
	double xmult, ymult;
	int xoff, yoff;

	makeNodes(coded_message);
	setInsAndOuts();

	xmult = (width - 2.0*margin)/(xnodes - 1.0);
	xmult = Math.min(xmult,(double)max_line);
	xoff = (int)((width - xmult*(xnodes-1.0))/2.0);

	ymult = (height - 2.0*margin)/(ynodes - 1.0);
	ymult = Math.min(ymult,(double)max_line);
	yoff = (int)((height - ymult*(ynodes-1.0))/2.0);
	
	while (vi.hasNext()) {
	    e = (AnimationTreeNode)(vi.next());
	    e.setX((int)(xoff + e.getX()*xmult));
	    e.setY((int)(yoff + e.getY()*ymult));
	}

	invertYCoords(height);
	return nodes.size();
    }



    /*
      Verification methods
    */

    // Verifier - check that l is in valid line code
    private boolean checkWord(String l)
    {
	StringTokenizer lines = new StringTokenizer(l," ",false);
	int i;

	while (lines.hasMoreTokens()) {
	    String line = lines.nextToken();

	    if (line.charAt(0) == 'C') {
		if (line.length()==2 && Character.isDigit(line.charAt(1)))
		    continue;
		return false;
	    }

	    StringTokenizer points = new StringTokenizer(line,"-",false);
	    while (points.hasMoreTokens()) {
		String point = points.nextToken();
		for (i=0; i<point.length(); i++) {
		    if (point.charAt(i) == ',') break;
		    if (!(Character.isDigit(point.charAt(i)))) return false;
		}
		i++;
		if (i==point.length()) return false;
		for (; i<point.length(); i++) {
		    if (!(Character.isDigit(point.charAt(i)))) return false;
		}
	    }
	}
	return true;
    }

    // Verifier - check if alphabet is in valid line-code
    private void checkAlphabet()
    {
	int i;
	for (i=0; i<alphabet.length; i++)
	    if (!checkWord(alphabet[i])) {
		System.err.println("Bad letter " + i);
		System.exit(1);
	    }
    }
}
