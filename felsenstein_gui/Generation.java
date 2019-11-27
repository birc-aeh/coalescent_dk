

import java.util.*;

/** 
 * One generation, ie. the nodes in one line of the animation
 * 
 * @author Anders Mikkelsen
 */
public class Generation extends Vector
{
    /** 
     * The number of nodes in this generation
     */
    private int n;

    /** 
     * The id of this generation in the complete set of generation, ie.
     * id of the row occupied by the nodes in this generation
     */
    private int number;
    
    /** 
     * Make a new generation
     * 
     * @param parent the parent, ie. the set of generations that this 
     *     generation belongs to
     */
    public Generation(Generations parent)
    {
	super(parent.genes());
	this.n = parent.genes();

	int i;
	for (i=0; i<n; i++)
	    addElement(new Node());
    }

    /** 
     * Set the row id of this generation
     * 
     * @param number the new row id
     */
    public void setNumber(int number)
    {
	this.number = number;
    }

    /** 
     * Put node in this generation
     * 
     * @param i index at which the new node is put
     * @param n the new node
     */
    public void set(int i, Node n)
    {
	setElementAt(n,i);
    }

    /** 
     * Get a node from this generation
     * 
     * @param i the index from which to retrieve a node
     * @return the node at index i
     */
    public Node node(int i)
    {
	return (Node)(elementAt(i));
    }

    /** 
     * Get the number of nodes in this generation
     * 
     * @return number of nodes in this generation
     */
    public int genes() { return n; }

    /** 
     * Get row id of this generation
     * 
     * @return row id of this generation
     */
    public int number() { return number; }

}
