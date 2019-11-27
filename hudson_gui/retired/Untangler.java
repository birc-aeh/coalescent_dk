
import java.util.*;


public class Untangler
{
    Random r;

    public Untangler()
    {
	r = new Random();
    }

    /**
     * Return pseudo random integer in range [0;max[
     *
     * @param max the upper limit
     */
    private int rndInt(int max) { return r.nextInt()%max; }


    /**
     * Add int to Integer vector if not already there
     *
     * @param v   the Integer vector
     * @param val the value to add
     */
    private void addToIntVector(Vector v, int val)
    {
	VectorIterator vi = new VectorIterator(v);
	Integer i;

	while (vi.hasNext()) {
	    i = (Integer)(vi.next());
	    if (i.intValue() == val) return;
	}

	v.addElement(new Integer(val));
    }

    private AnimationTreeNode getNode(Vector v, int i)
    {
	VectorIterator vi = new VectorIterator(v);
	while (vi.hasNext()) {
	    AnimationTreeNode n = (AnimationTreeNode)(vi.next());
	    if (n.ID == i) return n;
	}
	return null;
    }



    /**
     * Try to untangle the graph (this is not easy!)
     *
     * @param n     the root of untanglement
     * @param from  the boundary the node must be between
     * @param to    the boundary the node must be between
     */
    private void untangleAll(AnimationTreeNode n, int from, int to)
    {
	Vector in = n.getIns();
	AnimationTreeNode a;
	VectorIterator vi;
	int x, cwidth;

	x = from + (to-from)/2;
	n.x = x;
	//System.out.println(n.getID() + ":" + from + " -> " + to + ":" + x);
	
	if (in.size() == 0) return;

	Vector ids = new Vector();
	vi = new VectorIterator(in);
	while (vi.hasNext()) {
	    a = (AnimationTreeNode)(vi.next());
	    addToIntVector(ids,a.ID);
	}

	cwidth = (to-from)/ids.size();
	vi = new VectorIterator(ids);
	while (vi.hasNext()) {       
	    untangleAll(getNode(in,((Integer)(vi.next())).intValue()),
			  from, from+cwidth);
	    from += cwidth;
	}
    }


    private int id_counter;
    private void scaleBottom(AnimationTreeNode n)
    {
	VectorIterator vi = new VectorIterator(n.getIns());

	if (n.getIns().size() == 0)
	    n.ID = id_counter++;
	
	while (vi.hasNext())
	    scaleBottom((AnimationTreeNode)(vi.next()));
    }


    public void untangle(AnimationTreeNode n, int width, int height)
    {
	untangleAll(n,width,height);
	id_counter = 0;
	scaleBottom(n);
    }







}
