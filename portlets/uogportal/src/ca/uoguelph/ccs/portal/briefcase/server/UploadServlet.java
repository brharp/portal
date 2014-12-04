package ca.uoguelph.ccs.portal.briefcase.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.BufferedInputStream;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.webdav.lib.WebdavResource;

public class UploadServlet extends HttpServlet
{
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        ServletRequestContext ctx = new ServletRequestContext(req);
        if (ServletFileUpload.isMultipartContent(ctx)) {
            FileItemFactory fac = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(fac);
            try {
                String path = null;
                String name = null;
                byte[] file = null;
                File   disk = new File("C:/upload.txt");
                List items = upload.parseRequest(req);
                Iterator iter = items.iterator();
                while(iter.hasNext()) {
                    FileItem item = (FileItem)iter.next();
                    if (item.isFormField()) {
                        if ("path".equals(item.getFieldName())) {
                            path = item.getString();
                            System.out.println("UploadSErvlet: path = " + path);
                        } else if ("name".equals(item.getFieldName())) {
                            name = item.getString();
                            System.out.println("UploadServlet: name = " + name);
                        }
                    } else {
                        file = item.get();
                        System.out.println("UploadServlet: file = " + new String(file));
                    }
                }
                if (path == null || name == null) {
                    throw new ServletException("Path or name are null");
                }
                try {
                    WebdavResource dav = new WebdavResource
                        (new HttpURL("brharp", "xew998ex", "www.cfs.uoguelph.ca", 80, path));
                    dav.setDebug(Integer.MAX_VALUE);
                    dav.propfindMethod(0);
                    if (dav.exists()) {
                        if (!dav.putMethod(path + "/" + name, file)) {
                            throw new ServletException("Upload failed: " + 
                                                       dav.getStatusCode() + " " +
                                                       dav.getStatusMessage());
                        }
                    } else {
                        throw new ServletException(path + " does not exist!");
                    }
                } catch(IOException e) {
                    throw new ServletException (e.getMessage(), e);
                }
            }
            catch(FileUploadException e) {
                throw new ServletException(e);
            }
            PrintWriter out = res.getWriter();
            out.println("FIle uploaded successfully.");
        }
        else {
            throw new ServletException("Error: Content is not multipart/form-data.");
        }
    }
}
