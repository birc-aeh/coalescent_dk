
/**
 * Thrown if the information supplied by the CGI-script
 * is invalid in some way.
 *
 * @author Anders Mikkelsen
 */
public class TreeFormatException extends Exception
{
    public TreeFormatException() { super(); }

    public TreeFormatException(String s) { super(s); }
}
