
package ca.uoguelph.ccs.db.schema;

import ca.uoguelph.ccs.db.schema.xml.XMLSchemaReader;
import ca.uoguelph.ccs.db.schema.jdbc.JDBCSchemaReader;
import java.net.URI;

public class SchemaSourceFactory
{
    private SchemaSourceFactory()
    {
    }

    public static SchemaSourceFactory getInstance()
    {
        return new SchemaSourceFactory();
    }

    public SchemaSource getSchemaSource(URI uri)
    {
        String protocol = uri.getScheme();

        if("file".equals(protocol)) {
            return new XMLSchemaReader(uri.toString());
        }
        else if ("jdbc".equals(protocol)) {
            return new JDBCSchemaReader(uri.toString());
        }
        else {
            return null;
        }
    }
}
