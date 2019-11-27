
import java.awt.*;

/** 
 * Class implementing a small set of colors with methods for iterating 
 * through the set.
 * 
 * @author Anders Mikkelsen
 */
public class Colors
{

    /** 
     * Iterator variables
     * 
     */
    private static int ncols, next;
    /** 
     * The set of colors
     * 
     */
    private static Color color[];

    /** 
     * Initialize the set. The initializer defines the colors in the set
     * on its own
     * 
     * @param n number of colors in the set
     */
    static void init(int n)
    {
	double grad, c;
	int i;

	grad = 1.0/(double)(n-1);
	c = 0;
	color = new Color[Math.max(2,n)];
	color[0] = new Color(0.2F,0.2F,1.0F);
	color[1] = new Color(0.2F,1.0F,0.2F);

	for (i=2; i<n; i++) {
	    color[i] = new Color((float)c,(float)c,(float)0.6);
	    c += grad;
	}
	ncols = n;
	rewind();
    }

    /** 
     * Iterator - return next color in the set
     * 
     * @return next color in the set
     */
    static Color next()
    {
	int c;
	c = next;
	next = (next+1)%ncols;
	return color[c];	
    }

    /** 
     * Iterator - rewind to first color in the set
     * 
     */
    static void rewind()
    {
	next = 0;
    }

}


