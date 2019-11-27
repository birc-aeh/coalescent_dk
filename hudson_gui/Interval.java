
/**
 * An interval [from,to] (or whatever interpretation you prefer)
 *
 * @author Anders Mikkelsen
 */
public class Interval
{
    /// Interval endpoints
    private double from, to;

    /**
     * Make new interval with endpoints
     *
     * @param from left endpoint
     * @param to   right endpoint
     */
    public Interval(double from, double to)
    {
	this.from = from;
	this.to = to;
    }

    /**
     * Get left endpoint
     * 
     * @return left endpoint
     */
    public double getFrom() { return from; }

    /**
     * Get right endpoint
     *
     * @return right endpoint
     */
    public double getTo() { return to; }

}
