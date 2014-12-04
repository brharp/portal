package ca.uoguelph.ccs.db.query;

import ca.uoguelph.ccs.db.schema.*;

public class CreateSequenceQuery extends Query
{
	private Sequence sequence;
	
	public CreateSequenceQuery(Sequence sequence)
	{
		this.sequence = sequence;
	}

	public void process(QueryProcessor p)
	{
		p.createSequence(sequence);
	}

}
