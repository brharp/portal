package ca.uoguelph.ccs.portal.services.proxy;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.jasig.portal.security.*;
import org.jasig.portal.security.provider.*;

/**
 * Provides an AJAX interface to a Squid HTTP proxy. AJAX requests for
 * resources beyond the origin server must be handled by a proxy, as
 * the browser security model only allows scripts to access resources
 * served by the origin server. This class provides scripts with any
 * interface to an HTTP proxy server, avoiding the need for an ad-hoc
 * HTTP proxy implementation.
 *
 * A script makes a proxy request by posting a full HTTP/1.1 request
 * to this servlet. The request will be piped, intact, to the HTTP
 * proxy server, and the response returned, intact, to the requesting
 * script.
 * 
 */
public class SquidProxy extends HttpServlet
{
    private String proxyHost;
    private int    proxyPort;

    public static final int MAXHDR = 1659;
    public static final int BUFSIZ = 4096;
    
    public static final int CR = 13;
    public static final int LF = 10;

    public static final String INIT_PROXY_HOST = "proxy-host";
    public static final String INIT_PROXY_PORT = "proxy-port";

    /**
     * The servlet must be configured with the address and port number
     * of an HTTP proxy server.
     */
    public void init()
        throws ServletException
    {
        try {
            proxyHost = getInitParameter(INIT_PROXY_HOST);
            proxyPort = Integer.parseInt(getInitParameter(INIT_PROXY_PORT));
        }
        catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    /**
     * Pipe the request to the proxy server, and return the proxy
     * server response.
     */
    protected void doPost(HttpServletRequest q,
                          HttpServletResponse p)
        throws ServletException, IOException
    {
        Socket s;
        InputStream i;
        OutputStream o;
        byte b[] = new byte[BUFSIZ];

        try {
            // Connect to proxy.
            s = new Socket(proxyHost, proxyPort);

            // Write request.
            i = q.getInputStream();
            o = s.getOutputStream();

            int c;
            while((c = i.read(b)) > -1) {
                o.write(b, 0, c);
            }

            //i.close();
            //o.close();

            // Read response.
            i = s.getInputStream();
            o = p.getOutputStream();

            // Skip protocol version.
            while((c = i.read()) > -1) {
                if (c == ' ') break;
            }
            
            byte[] stat = new byte[3];
            i.read(stat);
            log("Status: " + new String(stat));
            int code = parseStatus(stat);
            p.setStatus(code);

            do c = i.read();    // Skip reason.
            while (c > -1 && c != LF);

            // Parse headers.
            byte[] h = new byte[MAXHDR];
            while ((c = readLine(i, h)) > 0) {
                int n = 0; // Find end of name.
                while(n < c && h[n] != ':') 
                    n++;
                int v = n + 1; // Find start of value.
                while(v < c && h[v] == ' ') 
                    v++;
                String hn = new String(h, 0, n);
                String hv = new String(h, v, c - v);
                p.setHeader(hn, hv);
            }

            // Send body.
            while ((c = i.read(b)) > -1) {
                o.write(b, 0, c);
            }

            s.close();
        }
        catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private int readLine(InputStream in, byte[] buf)
        throws IOException
    {
        int c, n = 0;

        while ((c = in.read()) > -1) {
            if (c == CR) continue;
            if (c == LF) break;
            buf[n++] = (byte)c;
        }
        
        return n;
    }

    private int parseStatus(byte[] stat)
    {
        int code = 0;
        for (int i = 0; i < stat.length; i++) {
            code *= 10;
            code += stat[i] - '0';
        }
        return code;
    }
}
