
import java.util.*;

/** 
 * The Wright-Fisher model of evolution
 * 
 * @author Anders Mikkelsen
 */
public class WrightFisherModel extends Model
{
    /** 
     * Random number generator
     * 
     */
    private Random rnd;

    /** 
     * Make a new model
     * 
     * @param n number og columns (samples)
     * @param ngen number of generations
     */
    public WrightFisherModel(int n, int ngen)
    {
	super(n,ngen);

	rnd = new Random();
    }

    /** 
     * Get a random integer from the random stream
     * 
     * @return a random positive integer
     */
    private int rndInt()
    {
	return Math.abs(rnd.nextInt());
    }

    /** 
     * Evolve one generation
     * 
     * @return true if this was the last generation, false otherwise
     */
    public boolean evolveOneStep()
    {
	Generation ogen = g.lastGeneration();
	Generation ngen = new Generation(g);
	int i;

	for (i=0; i<n; i++) {
	    Node s = ngen.node(i);
	    Node k = ogen.node(rndInt()%n);
	    k.addSibling(s);	    
	}

	g.add(ngen);
	
	return true;
    }


}
