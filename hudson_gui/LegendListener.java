
/**
 * Classes that react on Legend updates must
 * implement this interface
 *
 * @author Anders Mikkelsen
 */
public interface LegendListener
{
    /**
     * Add entry to legend
     *
     * @param color the color of the new entry
     * @param s the description of the new entry
     */
    public void addLegend(int color, String s);

    /**
     * Remove all entries
     */
    public void clearLegend();

}
