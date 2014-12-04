package ca.uoguelph.ccs.portal.portlets.databasestatsreporterportlet;

/**
 * A StatsQueryFilter used to specify a more granular context of a Stats
 * Recorder query. For example, without a query filter, all channel stats in the
 * portal may be queried for; by specifying and configuring a query filter, the
 * same stats can be narrowed down to stats for a particular user, channel,
 * stats event type, etc.
 * 
 * @author jtey
 *  
 */
/**
 * @author jtey
 *  
 */
public class StatsQueryFilter {

    private int userId;

    private String userName;

    private int hostId;

    private int eventType;

    private String startDate;

    private String endDate;

    public static final int NULL = -99;

    /**
     * Initialize all flags to "zero".
     */
    public StatsQueryFilter() {
        this.userId = NULL;
        this.userName = null;
        this.hostId = NULL;
        this.eventType = NULL;
    }

    /**
     * @return Returns the eventType.
     */
    public int getEventType() {
        return eventType;
    }

    /**
     * @param eventType
     *            The eventType to set.
     */
    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    /**
     * @return Returns the hostId.
     */
    public int getHost() {
        return hostId;
    }

    /**
     * @param hostId
     *            The hostId to set.
     */
    public void setHost(int hostId) {
        this.hostId = hostId;
    }

    /**
     * @return Returns the userId.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @return Returns the username.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userId
     *            The userId to set.
     */
    public void setUser(int userId) {
        this.userId = userId;
    }

    /**
     * @return Returns the endDate.
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * @param y
     * @param m
     * @param d
     */
    public void setEndDate(int y, int m, int d, int hr, int min) {
        this.endDate = y + "-" + m + "-" + d + " " + hr + ":" + min;
    }

    /**
     * @return Returns the startDate.
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * @param y
     * @param m
     * @param d
     */
    public void setStartDate(int y, int m, int d, int hr, int min) {
        this.startDate = y + "-" + m + "-" + d + " " + hr + ":" + min;
    }

    public String getWhereClause() {
        String wc = "";

        wc += this.getHost() == NULL ? "" : " AND StatTable.host_id="
                + this.getHost();
        wc += this.getUserId() == NULL ? "" : " AND uu.user_id="
                + this.getUserId();
        wc += this.getUserName() == null ? "" : " AND uu.user_name='"
                + this.getUserName() + "'";
        wc += this.getEventType() == NULL ? "" : " AND StatTable.type_id="
                + this.getEventType();
        wc += this.getStartDate() == null ? "" : " AND StatTable.time>='"
                + this.getStartDate() + "'";
        wc += this.getEndDate() == null ? "" : " AND StatTable.time<='"
                + this.getEndDate() + "'";

        return wc;
    }

    public String toString() {
        return this.getWhereClause();
    }
}