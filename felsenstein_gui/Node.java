
import java.util.*;
import java.awt.*;

/** 
 * Node (gene) in the main animation
 * 
 * @author Anders Mikkelsen
 * @see Generation
 */
public class Node
{
    /** 
     * Number of siblings
     * 
     */
    public int sibling_count;

    /** 
     * Siblings (children)
     * 
     */
    public Vector siblings;

    /** 
     * Parent (there is only one parent)
     * 
     */
    public Node parent;

    /** 
     * Number of highlighted paths passing this node. The idea is to
     * add one to this number whenever a new highlighted path passes
     * the node and subtract one when a highlighted path is removed.
     * When the count reaches zero, the highlight is removed.
     * 
     */
    public int paths_passing;

    /** 
     * Coordinates
     * 
     */
    public int x,y,diameter;

    /** 
     * Is this node clickable
     * 
     */
    boolean clickable;

    /** 
     * Is this node ancestral (ie. should it be marked in the top row
     * of the animation)
     * 
     */
    boolean ancestor;

    /** 
     * The color of the highlight path passing this node
     * 
     */
    public Color path_color;

    /** 
     * Make a new node
     * 
     */
    public Node()
    {
	sibling_count = 0;
	siblings = new Vector();       
	diameter = 8;
	paths_passing = 0;
	clickable = false;
	ancestor = false;
    }

    /** 
     * Add a sibling to this node
     * 
     * @param n the new sibling
     */
    public void addSibling(Node n)
    {
	siblings.addElement(n);
	sibling_count++;
	n.parent = this;
    }
}
