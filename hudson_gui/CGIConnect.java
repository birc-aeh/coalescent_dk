
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Small class to invoke a CGI-script (GET method only) and collect
 * the output from the script
 *
 * @author Anders Mikkelsen
 */
public class CGIConnect 
{
    /// The URL of the CGI-script
    static URL url;
    
    /**
     * Set the URL of the CGI-script
     *
     * @param cgi_url the URL of the script
     */
    public static void setScript(URL cgi_url)
    {
	url = cgi_url;
    }

    /**
     * Set the URL of the CGI-script
     *
     * @param cgi_url the URL of the script presented as a string
     */
    public static void setScript(String cgi_url) throws MalformedURLException
    {
	url = new URL(cgi_url);
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
	Socket home = new Socket(url.getHost(), 80);
	String request;

	BufferedReader res = new BufferedReader(new InputStreamReader(home.getInputStream()));
	PrintWriter pwr = new PrintWriter(new BufferedWriter(new OutputStreamWriter(home.getOutputStream())));

	request = "GET " + url + "?" + param + "\r\n";
	pwr.print(request);
	pwr.flush();
     
	return res;
    }
}
