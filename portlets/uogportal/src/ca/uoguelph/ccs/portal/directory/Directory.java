
package ca.uoguelph.ccs.portal.directory;

import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.ldap.*;
import javax.portlet.*;

public class Directory extends GenericPortlet
{
    public static final String RESULTS_ATTRIBUTE 
        = "ca.uoguelph.ccs.portal.directory.RESULTS";

    public static final int RESULTS_SCOPE 
        = PortletSession.APPLICATION_SCOPE;
    
    public void processAction(ActionRequest request, ActionResponse response)
        throws PortletException
    {
        String firstName = request.getParameter("firstName");
        String firstNameBeginsWith = request.getParameter("firstNameBeginsWith");
        String lastName = request.getParameter("lastName");
        String lastNameBeginsWith = request.getParameter("lastNameBeginsWith");
        
        try {
            if (lastName != null && lastName.length() > 0) {
                StringBuffer q = new StringBuffer();
                q.append("(&");
                q.append(" (sn=");
                q.append(lastName);
                if (lastNameBeginsWith != null) {
                    q.append("*");
                }
                q.append(")");
                if (firstName != null && firstName.length() > 0) {
                    q.append(" (givenname=");
                    q.append(firstName);
                    if (firstNameBeginsWith != null) {
                        q.append("*");
                    }
                    q.append(")");
                }
                q.append(")");
                String query = q.toString();
                System.out.println("query="+query);
                DirContext ctx = new InitialDirContext();
                NamingEnumeration answer = ctx.search
                    ("ldap://directory.uoguelph.ca:389/ou=people,o=uoguelph.ca",
                     query, null);
                List results = new LinkedList();
                while(answer.hasMore()) {
                    SearchResult nextResult = (SearchResult)answer.next();
                    Map entry = new HashMap();
                    NamingEnumeration attributes = nextResult.getAttributes().getAll();
                    while(attributes.hasMore()) {
                        Attribute nextAttribute = (Attribute)attributes.next();
                        NamingEnumeration values = nextAttribute.getAll();
                        if(values.hasMore()) {
                            entry.put(nextAttribute.getID(), values.next());
                        }
                        values.close();
                    }
                    results.add(entry);
                }
                ctx.close();

                request.getPortletSession()
                    .setAttribute(RESULTS_ATTRIBUTE, results, RESULTS_SCOPE);
            } else {
                request.getPortletSession()
                    .setAttribute(RESULTS_ATTRIBUTE, null, RESULTS_SCOPE);
            }
        }
        catch(Exception e) {
            throw new PortletException(e);
        }
    }

    public void doView(RenderRequest request, RenderResponse response)
        throws PortletException
    {
        try {
            PortletContext context = getPortletContext();
            PortletRequestDispatcher dispatch = 
                context.getRequestDispatcher("/WEB-INF/jsp/directory/search.jsp");
            PortletSession session = request.getPortletSession();
            Object results = session.getAttribute(RESULTS_ATTRIBUTE, RESULTS_SCOPE);
            session.setAttribute(RESULTS_ATTRIBUTE, null, RESULTS_SCOPE);
            request.setAttribute("results", results);
            dispatch.include(request, response);
        }
        catch(Exception e) {
            throw new PortletException(e);
        }
    }
}
