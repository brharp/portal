/*
 * Created on 24-Nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ca.uoguelph.ccs.portal.services.stats;

/**
 * @author jtey
 *  
 */
public class PortalHost {

    public static final int happy = 1;

    public static final int P1 = 1;

    public static final int doc = 2;

    public static final int P2 = 2;

    public static final int bashful = 3;

    public static final int P3 = 3;

    public static final int sneezy = 4;

    public static final int P4 = 4;

    public static final int grumpy = 5;

    public static final int P5 = 5;

    public static final int sleepy = 6;

    public static final int QA1 = 6;

    public static final int dopey = 7;

    public static final int QA2 = 7;

    public static final int snowwhite = 8;

    public static final int L1 = 8;

    public static final int dragonfly = 9;

    public static final int DEV1 = 9;

    public static final int NULL = 0;

    static int getHostId() {
        int hostId = NULL;
        try {
            java.net.InetAddress localMachine = java.net.InetAddress
                    .getLocalHost();
            String hostname = localMachine.getHostName();
            /*
             * coded host names for the production portal "P?" coded host names
             * for the qa portal "QA?"
             */
            if ("happy".equals(hostname)) {
                hostId = P1;
            } else if ("doc".equals(hostname)) {
                hostId = P2;
            } else if ("bashful".equals(hostname)) {
                hostId = P3;
            } else if ("sneezy".equals(hostname)) {
                hostId = P4;
            } else if ("grumpy".equals(hostname)) {
                hostId = P5;
            } else if ("sleepy".equals(hostname)) {
                hostId = QA1;
            } else if ("dopey".equals(hostname)) {
                hostId = QA2;
            } else if ("snowwhite".equals(hostname)) {
                hostId = L1;
            } else if ("dragonfly".equals(hostname)) {
                hostId = DEV1;
            }
        } catch (java.net.UnknownHostException uhe) {
        }
        return hostId;
    }

}