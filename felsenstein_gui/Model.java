

/** 
 * Base class for evolutionary models
 * 
 * @author Anders Mikkelsen
 */
public class Model
{
    /** 
     * Number of nodes (genes) in each generation
     * 
     */
    protected int n;

    /** 
     * Number of generations (rows)
     * 
     */
    protected int ngen;

    /** 
     * Set of generations
     * 
     */
    protected Generations g;
    
    /** 
     * Make a new model
     * 
     * @param n number of genes (columns)
     * @param ngen number of generations (rows)
     */
    public Model(int n, int ngen)
    {
	int i;

	this.n = n;
	this.ngen = ngen;
	reset();
    }

    /** 
     * Delete all generations in this model
     * 
     */
    public void reset()
    {
	g = new Generations(n);
	g.add(new Generation(g));
    }

    /** 
     * Do one step of evolution - this method should be inherited
     * by subclasses.
     * 
     * @return true if this is the last step in the evolution, false otherwise
     */
    public boolean evolveOneStep()
    {
	Generation ogen = g.lastGeneration();
	Generation ngen = new Generation(g);
	int i;

	for (i=0; i<n; i++) {
	    Node s = ogen.node(i);
	    Node k = ngen.node((i+1)%n);
	    s.addSibling(k);
	}
	g.add(ngen);
	
	return true;
    }

    /** 
     * Do a complete evolution (ie. perform a number of one-step
     * evolutions)
     * 
     */
    public final void evolve()
    {
	int i;
	for (i=0; i<ngen; i++)
	    if (!evolveOneStep()) break;
    }

    /** 
     * Get the set of generations associated with this model
     * 
     * @return the set of generations associated with this model
     */
    public Generations generations() { return g; }

}
