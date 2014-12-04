package ca.uoguelph.ccs.db.schema.xml;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ca.uoguelph.ccs.db.schema.Constraint;
import ca.uoguelph.ccs.db.schema.Schema;
import ca.uoguelph.ccs.db.schema.SchemaSource;
import ca.uoguelph.ccs.db.schema.Table;

import org.springframework.util.SystemPropertyUtils;

public class XMLSchemaReader implements SchemaSource
{
    private InputSource inputSource;

    public XMLSchemaReader()
    {
    }

    public XMLSchemaReader(String uri)
    {
        setUri(uri);
    }

    public XMLSchemaReader(InputSource inputSource)
    {
        setInputSource(inputSource);
    }

    public void setUri(String uri)
    {
        setInputSource(new InputSource(SystemPropertyUtils.resolvePlaceholders(uri)));
    }

    public void setInputSource(InputSource inputSource)
    {
        this.inputSource = inputSource;
    }

    public Schema getSchema() throws Exception
    {
        SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        SchemaHandler handler = new SchemaHandler();
        saxParser.parse(inputSource, handler);
        return handler.getSchema();
    }

    class SchemaHandler extends DefaultHandler
    {
        private Schema schema;

        private Table table;

        private Constraint constraint;

        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException
        {
            if ("schema".equalsIgnoreCase(qName)) {
                schema = new Schema();
            } else if ("table".equalsIgnoreCase(qName)) {
                table = schema.createTable(attributes.getValue("name"));
            } else if ("column".equalsIgnoreCase(qName)) {
                table.createColumn(attributes.getValue("name"), attributes
                                   .getValue("type"), attributes.getValue("default"),
                                   Boolean.valueOf(attributes.getValue("not-null"))
                                   .booleanValue());
            } else if ("constraint".equalsIgnoreCase(qName)) {
                constraint = table.createConstraint(
                                                    attributes.getValue("name"), attributes
                                                    .getValue("type"));
            } else if ("key".equalsIgnoreCase(qName)) {
                constraint.addKey(attributes.getValue("name"));
            } else if ("sequence".equalsIgnoreCase(qName)) {
                schema.createSequence(attributes.getValue("name"));
            }
        }

        public Schema getSchema()
        {
            return schema;
        }
    }
}
