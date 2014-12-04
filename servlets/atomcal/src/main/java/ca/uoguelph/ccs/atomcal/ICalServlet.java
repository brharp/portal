package ca.uoguelph.ccs.atomcal;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import oracle.calendar.sdk.*;

public class ICalServlet extends HttpServlet
{  
    public static final String DEFAULT_START = "-P0D";
    public static final String DEFAULT_END   = "+P5W";

    private String host = new String();
    private List extendedIdAttributes = new ArrayList();

    public void init()
    {
        try {
            Api.init(getServletContext().getRealPath("calendar.ini"),
                     getServletContext().getRealPath("calendar.log"));
        }
        catch (Api.StatusException e) {
            log(e.getMessage());
        }

        String hostParam = getInitParameter("host");
        if (hostParam != null) {
            host = hostParam;
        }
        log("[host="+host+"]");
        
        String extIdAttrParam = getInitParameter("extendedIdAttributes");
        if (extIdAttrParam != null) {
            extendedIdAttributes = Arrays.asList(extIdAttrParam.split(","));
        }
        log("[extendedIdAttributes="+splice(extendedIdAttributes,',')+"]");
    }

    private String splice(List strings, char sep)
    {
        StringBuffer result = new StringBuffer();
        for (Iterator i = strings.iterator(); i.hasNext();) {
            result.append(String.valueOf(i.next()));
            if (i.hasNext())
                result.append(sep);
        }
        return result.toString();
    }

    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
        throws ServletException,
               IOException
    {
        doGet(req, resp);
    }

    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
        throws ServletException, 
               IOException
    {
        log("The default time zone is " + TimeZone.getDefault().getDisplayName());

        String iCalendar = new String();
        String userId = new String();
        String start = DEFAULT_START;
        String end = DEFAULT_END;
        String user = new String();
        String pass = new String();

        // Get the start and end parameters. If not specified,
        // defaults will be used.

        String startParam = req.getParameter("start");
        if (startParam != null) {
            start = startParam;
        }

        String endParam = req.getParameter("end");
        if (endParam != null) {
            end = endParam;
        }
        
        // Get credentials from the Authorization header. If no
        // credentials are supplied, return an authorization
        // challenge.
        
        String auth = req.getHeader("Authorization");
        log("Authorization: " + auth);
        if (auth == null || !auth.toUpperCase().startsWith("BASIC ")) {
            resp.setHeader("WWW-Authenticate", "BASIC realm=\"atomcal\"");
            resp.sendError(resp.SC_UNAUTHORIZED);
            return;
        }

        String cred = Base64.decodeToString(auth.substring(6));
        String[] part = cred.split(":", 2);
        String[] cn = part[0].split(" ", 2); // userid is "First Last".
        user = "?/S="+cn[1]+"/G="+cn[0]+"/";
        pass = part[1];

        // The user ID string may optionally start with a plain user
        // ID. If provided, this will be in the extra path info. The
        // user ID string may (and will most often) also contain
        // extended attributes. The set of request parameters to pass
        // on as extended user ID attributes are named by the 'ids'
        // list.
        
        StringBuffer userIdBuffer = new StringBuffer();
            
        if (req.getPathInfo() != null) {
            // Extra path info starts with a slash (/), which we don't
            // want. Use substring to trim the slash.
            userIdBuffer.append(req.getPathInfo().substring(1));
        }
            
        userIdBuffer.append("?");
            
        for (Iterator i = extendedIdAttributes.iterator(); i.hasNext();) {
            String id = ((String)(i.next())).trim();
            log("id="+id);
            String value = req.getParameter(id);
            log("value="+value);
            if (value != null) {
                userIdBuffer.append('/');
                userIdBuffer.append(id);
                userIdBuffer.append('=');
                userIdBuffer.append(value);
            }
        }
        
        userIdBuffer.append('/');
        userId = userIdBuffer.toString();
        userIdBuffer = null;
        log("[userId=" + userId + "]");
            
        try {
            // Connect to calendar.
                
            Session session = new Session();
                
            session.connect(Api.CAPI_FLAG_NONE, host);
                
            // Get agenda.
                
            try {
                session.authenticate(Api.CAPI_FLAG_NONE, user, pass);
                try {
                    // Retrieve agenda.
                        
                    Handle agendas[] = {new Handle()};
                        
                    agendas[0] = session.getHandle(Api.CAPI_FLAG_NONE, userId);
                        
                    log("Obtained handle for " + agendas[0].getType() + " " +
                        agendas[0].getEmail() + " [" + agendas[0].getName() + "]");
                        
                    String properties[]   = new String[0];
                    RequestResult results = new RequestResult();
                    iCalendar      = new String();
                    
                    iCalendar = session
                        .fetchEventsByRange(Api.CSDK_FLAG_STREAM_NOT_MIME,
                                            agendas,
                                            start,
                                            end,
                                            properties, 
                                            results);
                }
                catch(Api.StatusException e) {
                    throw new ServletException(e);
                }
                finally {
                    session.deauthenticate(Api.CAPI_FLAG_NONE);
                }
            } 
            catch (Api.StatusException e) {
                throw new ServletException(e);
            }
            finally {
                session.disconnect(Api.CAPI_FLAG_NONE);
            }
        }
        catch(Api.StatusException e) {
            throw new ServletException(e);
        }

        // Send response.
        
        resp.setContentType("text/calendar");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Expires", "-1");
        resp.getWriter().print(iCalendar);
    }
}

