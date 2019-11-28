
import java.io.*;
import java.net.*;
import java.util.*;
import javax.net.ssl.HttpsURLConnection;

/**
 * Small class to invoke a CGI-script (GET method only) and collect
 * the output from the script
 *
 * @author Anders Mikkelsen
 */
public class CGIConnect 
{
    /// The URL of the CGI-script
    static String base_url;
    
    /**
     * Set the URL of the CGI-script
     *
     * @param cgi_url the URL of the script presented as a string
     */
    public static void setScript(String cgi_url) throws MalformedURLException
    {
	base_url = cgi_url;
    }

    /**
     * Invoke the CGI-script with a given string of parameters. Usually the format
     * of the parameter string is <br>
     * <pre>
     * &lt;name&gt;=&lt;value&gt;&amp;&lt;name&gt;=&lt;value&gt;&amp;&lt;name&gt;=&lt;value&gt;&amp;...
     * </pre>
     *
     * @param param the parameter string
     * @return a buffered reader with the output from the script
     */
    public static BufferedReader invoke(String param) throws IOException
    {
	URL url = new URL(base_url + "?" + param);
        HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
        InputStream is = conn.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
	return br;
    }
}
