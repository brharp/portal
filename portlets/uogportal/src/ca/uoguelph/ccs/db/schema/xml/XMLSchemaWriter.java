
package ca.uoguelph.ccs.db.schema.xml;

import ca.uoguelph.ccs.db.schema.*;

import java.io.PrintWriter;
import java.util.Iterator;

public class XMLSchemaWriter implements SchemaVisitor
{
    private boolean inTable = false;
    private PrintWriter out;
    private StringBuffer xml;

    public XMLSchemaWriter(PrintWriter out)
    {
        this.out = out;
    }

    public void write(Schema schema)
    {
        xml = new StringBuffer();
        xml.append("<schema>\n");
        schema.accept(this);
        if (inTable) xml.append("  </table>\n");
        xml.append("</schema>\n");
        out.write(xml.toString());
        out.flush();
    }

    public boolean visit(Schema schema)
    { 
        return true;
    }

    public boolean visit(Table table)
    { 
        if (inTable) {
            xml.append("  </table>\n");
        }

        inTable = true;
        
        xml.append("  <table name=\"");
        xml.append(table.getName());
        xml.append("\">\n");

        return true;
    }

    public boolean visit(Sequence sequence)
	{
    	xml.append("  <sequence name=\"");
    	xml.append(sequence.getName());
    	xml.append("\">\n");
    	
		return false;
	}

	public boolean visit(Column column)
    {
        xml.append("    <column");
        xml.append(" name=\""); xml.append(column.getName()); xml.append("\"");
        xml.append(" type=\""); xml.append(column.getType()); xml.append("\"");
        if(column.getDefault() != null) {
            xml.append(" default=\""); xml.append(column.getDefault()); xml.append("\"");
        }
        if(column.isNotNull()) {
            xml.append(" not-null=\"true\"");
        }
        xml.append("/>\n");

        return true;
    }

    public boolean visit(Constraint constraint)
    {     
        xml.append("    <constraint");
        xml.append(" name=\""); xml.append(constraint.getName()); xml.append("\"");
        xml.append(" type=\""); xml.append(constraint.getType()); xml.append("\"");
        xml.append(">\n");
        
        if (Constraint.PRIMARY_KEY.equals(constraint.getType())) {
            for(Iterator it = constraint.keys(); it.hasNext();) {
                String nextKey = (String)it.next();
                xml.append("      <key name=\"");
                xml.append(nextKey);
                xml.append("\"/>\n");
            }
        }

        xml.append("    </constraint>\n");

        return true;
    }
}
