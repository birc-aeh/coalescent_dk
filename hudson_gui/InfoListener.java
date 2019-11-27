

/**
 * Interface implemented by information windows. Given a node
 * it should update the contents of the window.
 *
 * @author Anders Mikkelsen
 */
public interface InfoListener
{
    /**
     * Calling this should update the window
     *
     * @param n     the node to display info about
     * @param range the range of intervals for the given node
     */
    public void update(AnimationTreeNode n, Interval range, boolean selection);
}
