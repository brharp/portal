
package ca.uoguelph.ccs.db;

import ca.uoguelph.ccs.db.*;
import ca.uoguelph.ccs.db.query.*;
import ca.uoguelph.ccs.db.query.jdbc.*;
import ca.uoguelph.ccs.db.query.sql.*;
import ca.uoguelph.ccs.db.schema.*;
import ca.uoguelph.ccs.db.schema.jdbc.*;
import ca.uoguelph.ccs.db.schema.transform.*;
import ca.uoguelph.ccs.db.schema.xml.*;

import java.net.URI;
import java.io.PrintWriter;

public class SchemaTool
{
    public static void main(String args[])
        throws Exception
    {
        if (args.length < 1) {
            usage();
            return;
        }

        String command = args[0];
        
        if("dump".equals(command)) {
            if(args.length != 2) {
                usage();
                return;
            }
            URI schema1 = new URI(args[1]);
            dump(schema1);
        }
        else if("diff".equals(command)) {
            if(args.length != 3) {
                usage();
                return;
            }
            URI schema1 = new URI(args[1]);
            URI schema2 = new URI(args[2]);
            diff(schema1, schema2);
        }
        else if("transform".equals(command)) {
            if(args.length != 3) {
                usage();
                return;
            }
            URI schema1 = new URI(args[1]);
            URI schema2 = new URI(args[2]);
            transform(schema1, schema2);
        }
        else {
            System.err.println("Unrecognized command " + command);
        }
    }

    public static void diff(URI url1, URI url2)
        throws Exception
    {
        SchemaSourceFactory factory = SchemaSourceFactory.getInstance();

        SchemaSource source1 = factory.getSchemaSource(url1);
        SchemaSource source2 = factory.getSchemaSource(url2);

        Schema schema1 = source1.getSchema();
        Schema schema2 = source2.getSchema();

        QueryScript script = new QueryScript();

        schema2.accept(new SchemaTransformer(script, schema1));

        script.run(new SQLWriter(System.out));
    }

    public static void dump(URI url1)
        throws Exception
    {
        SchemaSourceFactory factory = SchemaSourceFactory.getInstance();
        SchemaSource source1 = factory.getSchemaSource(url1);
        Schema schema1 = source1.getSchema();

        new XMLSchemaWriter(new PrintWriter(System.out))
            .write(schema1);
    }

    public static void transform(URI url1, URI url2)
        throws Exception
    {
        SchemaSourceFactory factory = SchemaSourceFactory.getInstance();

        SchemaSource source1 = factory.getSchemaSource(url1);
        SchemaSource source2 = factory.getSchemaSource(url2);

        Schema schema1 = source1.getSchema();
        Schema schema2 = source2.getSchema();

        QueryScript script = new QueryScript();
        schema2.accept(new SchemaTransformer(script, schema1));
        
        String url = url1.toString();
        String user = System.getProperty("jdbc.user");
        String pass = System.getProperty("jdbc.password");
        
        JDBCQueryProcessor db = new JDBCQueryProcessor();

        try {
            db.connect(url, user, pass);
            script.run(db);
        }
        catch(DatabaseException dbe) {
            throw dbe;
        }
        finally {
            db.close();
        }
    }

    public static void usage()
    {
        System.err.println("Usage: ???");
    }
}
