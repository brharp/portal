/*
 * $Id: IsbnResource.java,v 1.1 2006/05/01 13:37:14 brharp Exp $
 *
 * Copyright 2006 University of Guelph
 */

package ca.uoguelph.ccs.portal.mycourses;

/**
 * A resource that points to a book in the bookstore.
 *
 * @author $Author: brharp $
 * @version $Revision: 1.1 $
 */
public class IsbnResource extends Resource
{
    private String isbn;

    public void setIsbn(String isbn)
    {
        this.isbn = isbn;
    }

    public String getIsbn()
    {
        return isbn;
    }

    public String getUrl()
    {
        return "http://www.amazon.com/gp/product/"+isbn;
    }
}
