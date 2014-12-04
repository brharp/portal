package ca.uoguelph.ccs.atomcal;

import java.io.*;
import java.util.*;
import javax.servlet.*;

public class AliasProperties extends Properties
    implements ServletContextListener
{
    private static File file = null;
    private static long lastRead = 0;
    private static Properties properties = new Properties();
    
    public static synchronized Properties getProperties()
    {
        if (lastRead < file.lastModified()) {
            try {
                FileInputStream in = new FileInputStream(file);
                properties.load(in);
                in.close();
                lastRead = System.currentTimeMillis();
                System.out.println("Loaded alias.properties.");
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        return properties;
    }
    
    public void contextInitialized(ServletContextEvent sce)
    {
        ServletContext sc = sce.getServletContext();
        file = new File(sc.getRealPath("alias.properties"));
    }

    public void contextDestroyed(ServletContextEvent sce) {}
}
