

import java.util.*;


/** 
 * A class implementing a set of generations, ie. a set of rows in the
 * animation
 * 
 * @author Anders Mikkelsen
 * @see Generation
 */
public class Generations extends Vector
{
    /** 
     * Number of nodes in each layer
     * 
     */
    private int n;

    /** 
     * Make new set of generations
     * 
     * @param n number of nodes in each generation
     */
    public Generations(int n)
    {
	super();
	this.n = n;
    }

    /** 
     * Get the number of generations in this set of generations
     * 
     * @return number of generations (layers)
     */
    public int layers() { return size(); }

    /** 
     * Add a generation to this set of generations
     * 
     * @param g the new generation to add
     */
    public void add(Generation g) {
	g.setNumber(size());
	addElement(g);
    }

    /** 
     * Retrieve a generation from this set
     * 
     * @param i the index of the generation to retrieve
     * @return the desired generation
     */
    public Generation generation(int i) { return (Generation)(elementAt(i)); }

    /** 
     * Get the youngest generation in this set
     * 
     * @return the youngest generation (ie. the one with the highest id)
     */
    public Generation lastGeneration()
    { 
	if (size() == 0) return null;
	return (Generation)(elementAt(size()-1));
    }

    /** 
     * Get the number of nodes in each generation
     * 
     * @return the number of nodes in each generation
     */
    public int genes() { return n; }


    /** 
     * Untangle this set of generation, ie. rearrange the nodes in each
     * layer such that no lines connecting them cross.
     * 
     */
    public void unTangle()
    {
	int i,j,k;
	Generation ogen, ngen;
	

	for (i=0; i<size()-1; i++) {
	    ogen = generation(i);
	    ngen = generation(i+1);
	    k = 0;
	    for (j=0; j<n; j++) {
		Node n = ogen.node(j);
		VectorIterator vi = new VectorIterator(n.siblings);
		while (vi.hasNext()) {
		    Node s = (Node)(vi.next());
		    ngen.set(k++,s);
		}
	    }
	}
    }

}
