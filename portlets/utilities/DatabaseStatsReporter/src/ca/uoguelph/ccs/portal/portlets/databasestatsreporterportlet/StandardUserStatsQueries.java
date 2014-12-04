package ca.uoguelph.ccs.portal.portlets.databasestatsreporterportlet;

import ca.uoguelph.ccs.portal.services.stats.StatsEvents;

/**
 * A collection of standard stats queries about user related statistics recorded
 * by the database stats recorder.
 * 
 * @author jtey
 *  
 */
public class StandardUserStatsQueries {

    public static String userStats() {
        return "SELECT ush.name as host, StatTable.time, uu.user_name as user, usuet.name as event FROM up_stats_user_event StatTable, up_stats_user_event_type usuet, up_stats_host ush, up_user uu WHERE StatTable.type_id=usuet.id AND StatTable.host_id=ush.id AND StatTable.user_id=uu.user_id ORDER BY StatTable.time DESC";
    }

    public static String numOfLogins() {
        return numOfLogins(new StatsQueryFilter());
    }

    public static String numOfLogins(StatsQueryFilter f) {
        return "SELECT count(1) as \"Unique Logouts\" FROM up_stats_user_event StatTable, up_user uu WHERE StatTable.user_id=uu.user_id AND StatTable.type_id="
                + StatsEvents.LOGIN + f;
    }

    /**
     * Returns the SQL query for number of unique users who have logged into the
     * portal (on all portal servers combined).
     * 
     * @return The SQL select statement.
     */
    public static String numOfUniqueUsers() {
        return numOfUniqueUsers(new StatsQueryFilter());
    }

    /**
     * Returns the SQL query for number of unique users who have logged into the
     * portal on the specified portal server.
     * 
     * @param server
     *            The portal server as defined in
     *            ca.uoguelph.ccs.portal.services.stats.PortalHost, or -1 to
     *            indicate "all servers"
     * @return The SQL select statement.
     */
    public static String numOfUniqueUsers(StatsQueryFilter f) {
        return "SELECT count(DISTINCT uu.user_id) as \"Unique Logins\" FROM up_stats_user_event StatTable, up_user uu WHERE StatTable.user_id=uu.user_id AND StatTable.type_id=1"
                + f;
    }

    public static String numOfLogouts() {
        return numOfLogouts(new StatsQueryFilter());
    }

    /**
     * Returns the number of logouts SQL query.
     * 
     * @param f
     *            The query filter to use.
     * @return The SQL select statement.
     */
    public static String numOfLogouts(StatsQueryFilter f) {
        return "SELECT count(1) as \"Logouts\" FROM up_stats_user_event StatTable, up_user uu WHERE StatTable.user_id=uu.user_id AND StatTable.type_id="
                + StatsEvents.LOGOUT + f;
    }

    public static String averageSessionTime() {
        return averageSessionTime(new StatsQueryFilter());
    }

    /**
     * Returns the SQL query for the average session time per user, with
     * optional granularity as specified by the StatsQueryFilter.
     * 
     * @param f
     *            The StatsQueryFilter to narrow down results to a specific
     *            context.
     * @return The SQL select statement.
     */
    public static String averageSessionTime(StatsQueryFilter f) {

        String dateRangeClause = "";

        /*
         * The average session time query is a non-standard query that is not
         * compatible with the stats filter's generated where clause, therefore,
         * it has to be constructed manually.
         */
        if (f.getStartDate() != null) {
            dateRangeClause += " AND start.time>='" + f.getStartDate() + "'";
            dateRangeClause += " AND finish.time>='" + f.getStartDate() + "'";
        }
        if (f.getEndDate() != null) {
            dateRangeClause += " AND start.time<='" + f.getEndDate() + "'";
            dateRangeClause += " AND finish.time<='" + f.getEndDate() + "'";
        }

        return "SELECT avg(length) FROM(SELECT (min(finish.time) - start.time) as length FROM up_stats_user_event start, up_stats_user_event finish, up_user uu WHERE uu.user_id=start.user_id "
                + dateRangeClause
                + " AND start.user_id=finish.user_id AND start.type_id="
                + StatsEvents.LOGIN
                + " AND finish.type_id="
                + StatsEvents.SESSION_DESTROYED
                + " AND finish.time > start.time GROUP BY start.time) as SessionLengths";
    }

}