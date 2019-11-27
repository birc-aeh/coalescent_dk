

/** 
 * Implement this interface to listen on impulse generators
 * 
 * @author Anders Mikkelsen
 */
public interface ImpulseListener
{
    /** 
     * Called by the impulse generator wheneven an impulse is generated
     * 
     * @return true if the impulse generator should continue generating 
     *     impulses, false otherwise
     */
    public boolean consumeImpulse();
}
