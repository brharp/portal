
package ca.uoguelph.ccs.db;

public class DatabaseException extends RuntimeException
{
    public DatabaseException(Throwable t)
    {
        super(t);
    }

    public DatabaseException(String detail)
    {
        super(detail);
    }

    public DatabaseException(String detail, Throwable t)
    {
        super(detail, t);
    }
}
