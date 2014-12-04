
package ca.uoguelph.ccs.db.schema;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;

public class Schema
{
    private Map map;

    public Schema()
    {
        this.map = new HashMap();
    }

    public void clear()
    {
        map.clear();
    }

    public boolean containsRelation(String name)
    {
        return map.containsKey(name);
    }
    
    public boolean containsRelation(Relation table)
    {
        return map.containsValue(table);
    }
    
    public Relation getRelation(String name)
    {
        return (Relation)map.get(name);
    }

    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    public Table createTable(String name)
    {
        Table t = new Table(this, name);
        addRelation(t);
        return t;
    }

    public Sequence createSequence(String name) 
    {
    	Sequence s = new Sequence(this, name);
    	addRelation(s);
    	return s;
    }    
    
    public void addRelation(Relation relation)
    {
        map.put(relation.getName(), relation);
    }
    
    public void addAllRelations(Schema s)
    {
        map.putAll(s.map);
    }

    public Relation removeRelation(Relation relation)
    {
        return removeRelation(relation.getName());
    }

    public Relation removeRelation(String name)
    {
        return (Relation)map.remove(name);
    }

    public int size()
    {
        return map.size();
    }

    public Collection relations()
    {
        return map.values();
    }

    public Set relationNames()
    {
        return map.keySet();
    }

    public void accept(SchemaVisitor v)
    {
        if (v.visit(this)) {
            for (Iterator it = relations().iterator(); it.hasNext();) {
                Relation next = (Relation)it.next();
                next.accept(v);
            }
        }
    }
}
