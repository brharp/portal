package ca.uoguelph.ccs.db;

import java.net.URI;
import java.io.PrintWriter;
import java.io.File;

import javax.servlet.*;

import ca.uoguelph.ccs.db.*;
import ca.uoguelph.ccs.db.query.*;
import ca.uoguelph.ccs.db.query.jdbc.*;
import ca.uoguelph.ccs.db.query.sql.*;
import ca.uoguelph.ccs.db.schema.*;
import ca.uoguelph.ccs.db.schema.jdbc.*;
import ca.uoguelph.ccs.db.schema.transform.*;
import ca.uoguelph.ccs.db.schema.xml.*;

import org.springframework.core.io.FileSystemResource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.web.util.WebUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;

public class SchemaContextListener implements ServletContextListener
{
    public static final String CONFIG_LOCATION_PARAM = "schemaConfigLocation";

    public static final String SOURCE_SCHEMA_BEAN = "sourceSchema";
    
    public static final String TARGET_SCHEMA_BEAN = "targetSchema";

    public static final String QUERY_PROCESSOR_BEAN = "queryProcessor";

    public void contextInitialized(ServletContextEvent sce)
    {
        ServletContext context = sce.getServletContext();

        context.log("Checking database schema");

        String location = context.getInitParameter(CONFIG_LOCATION_PARAM);
        if (location != null) {
            try {
                if (!ResourceUtils.isUrl(location)) {
                    location = SystemPropertyUtils.resolvePlaceholders(location);
                    location = WebUtils.getRealPath(context, location);
                }

                context.log("Initializing SchemaTool from [" + location + "]");

                BeanFactory beans = new XmlBeanFactory(new FileSystemResource(location));

                SchemaSource source1 = (SchemaSource)beans.getBean(SOURCE_SCHEMA_BEAN);
                SchemaSource source2 = (SchemaSource)beans.getBean(TARGET_SCHEMA_BEAN);
            
                Schema schema1 = source1.getSchema();
                Schema schema2 = source2.getSchema();

                QueryScript script = new QueryScript();
                
                schema2.accept(new SchemaTransformer(script, schema1));
            
                QueryProcessor processor = (QueryProcessor)beans.getBean(QUERY_PROCESSOR_BEAN);

                script.run(processor);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void contextDestroyed(ServletContextEvent sce)
    {
    }
}
