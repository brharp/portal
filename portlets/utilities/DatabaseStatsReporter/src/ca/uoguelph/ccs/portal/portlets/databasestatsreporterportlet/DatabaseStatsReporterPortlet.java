package ca.uoguelph.ccs.portal.portlets.databasestatsreporterportlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Calendar;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.jasig.portal.RDBMServices;

public class DatabaseStatsReporterPortlet extends GenericPortlet {

    private int numberOfLogins;

    private int numberOfUniqueUsers;

    private int numberOfLogouts;

    private String averageSessionLength;

    private int numberOfChannelPublishes;

    private int numberOfChannelRemovals;

    private int numberOfChannelModifications;

    private int numberOfChannelAddedToLayout;

    private int numberOfChannelRemovedFromLayout;

    private int numberOfChannelMovedInLayout;

    private int startYear;

    private int startMonth;

    private int startDay;

    private int startHour;

    private int startMinute;

    private int endYear;

    private int endMonth;

    private int endDay;

    private int endHour;

    private int endMinute;

    private int portalHost;

    /**
     *  
     */
    public DatabaseStatsReporterPortlet() {
        super();
        this.numberOfLogins = 0;
        this.numberOfUniqueUsers = 0;
        this.numberOfLogouts = 0;
        this.averageSessionLength = "N/A";
        this.numberOfChannelPublishes = 0;
        this.numberOfChannelRemovals = 0;
        this.numberOfChannelModifications = 0;
        this.numberOfChannelAddedToLayout = 0;
        this.numberOfChannelRemovedFromLayout = 0;
        this.numberOfChannelMovedInLayout = 0;

        /*
         * Determine the current date
         */
        Calendar rightNow = Calendar.getInstance();

        this.startYear = rightNow.get(Calendar.YEAR);
        this.startMonth = rightNow.get(Calendar.MONTH);
        this.startDay = rightNow.get(Calendar.DAY_OF_MONTH);
        this.startHour = 0;
        this.startMinute = 0;

        this.endYear = rightNow.get(Calendar.YEAR);
        this.endMonth = rightNow.get(Calendar.MONTH);
        this.endDay = rightNow.get(Calendar.DAY_OF_MONTH);
        this.endHour = 23;
        this.endMinute = 59;

        this.portalHost = -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request,
            ActionResponse actionResponse) throws PortletException,
            java.io.IOException {
        //corresponding action to test4.jsp part1:'checkAction'

        /*
         * Check if the preferences form was submitted
         */
        String statsPrefsSubmitted = request.getParameter("stats_prefs_submit");
        if (statsPrefsSubmitted != null) {

            this.startYear = Integer.parseInt(request
                    .getParameter("start_year"));
            this.startMonth = Integer.parseInt(request
                    .getParameter("start_month"));
            this.startDay = Integer.parseInt(request.getParameter("start_day"));
            this.startHour = Integer.parseInt(request
                    .getParameter("start_hour"));
            this.startMinute = Integer.parseInt(request
                    .getParameter("start_minute"));

            this.endYear = Integer.parseInt(request.getParameter("end_year"));
            this.endMonth = Integer.parseInt(request.getParameter("end_month"));
            this.endDay = Integer.parseInt(request.getParameter("end_day"));
            this.endHour = Integer.parseInt(request.getParameter("end_hour"));
            this.endMinute = Integer.parseInt(request
                    .getParameter("end_minute"));

        }
        String allTimeStats = request.getParameter("show_alltime_stats");
        if (allTimeStats != null) {

            this.startYear = 0;
            this.startMonth = 0;
            this.startDay = 0;
            this.startHour = 0;
            this.startMinute = 0;

            this.endYear = 0;
            this.endMonth = 0;
            this.endDay = 0;
            this.endHour = 0;
            this.endMinute = 0;

        }

    }

    /**
     * @return
     */
    private StatsQueryFilter getStatsFilter() {
        StatsQueryFilter f = new StatsQueryFilter();
        if (this.startDateIsDefined()) {
            f.setStartDate(this.startYear, this.startMonth, this.startDay,
                    this.startHour, this.startMinute);
        }
        if (this.endDateIsDefined()) {
            f.setEndDate(this.endYear, this.endMonth, this.endDay,
                    this.endHour, this.endMinute);
        }
        if (this.portalHost != -1) {
            f.setHost(this.portalHost);
        }

        System.out.println("filter: " + f);
        return f;
    }

    /**
     * Determines if a start date has been defined.
     * 
     * @return true if defined, false otherwise.
     */
    private boolean startDateIsDefined() {
        return (this.startYear != 0) && (this.startMonth != 0)
                && (this.startDay != 0);
    }

    /**
     * Determines if an end date has been defined.
     * 
     * @return true if defined, false otherwise.
     */
    private boolean endDateIsDefined() {
        return (this.endYear != 0) && (this.endMonth != 0)
                && (this.endDay != 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.print(this.getStatsFilterForm(response));

        /*
         *  
         */
        out.print(this.getSummaryStats());

        out.flush();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.portlet.GenericPortlet#doEdit(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doEdit(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        PortletURL renderURL = response.createRenderURL();
        renderURL.setPortletMode(PortletMode.VIEW);
        out.println("<a href=\"" + renderURL.toString()
                + "\"><b>Back to Stats</b></a><br />");
        // ...

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.portlet.GenericPortlet#doHelp(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doHelp(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        PortletURL renderURL = response.createRenderURL();
        renderURL.setPortletMode(PortletMode.VIEW);
        out.println("<a href=\"?" + renderURL.toString()
                + "\"><b>Back to Stats</b></a><br />");

        //out.print(this.getStatsFilterForm(response));

        String clrTag = "bgcolor=\"#EEEEEE\"";
        String str = "";

        str += "<table cellspacing=0 cellpadding=5>";
        str += "<tr><td><b>User Stats</b></td><td><b>Description</b></td></tr>";
        str += "<tr "
                + clrTag
                + "><td>Total logins</td><td>Number of logins that have been recorded</td></tr>";
        str += "<tr><td>Unique users</td><td>Number of unique users out of the total logins recorded</td></tr>";
        str += "<tr "
                + clrTag
                + "><td>Proper logouts</td><td>The number of logouts recorded, where the user has logged out by clicking \"Logout\"</td></tr>";
        str += "<tr><td>Average session length</td><td>An approximate average length of time that users are spending in the Portal, including the timeout period if the users did not logout properly</td></tr>";
        str += "<tr><td><b>Channel Stats</b></td><td><b>Description</b></td></tr>";
        str += "<tr "
                + clrTag
                + "><td>New channel publishes</td><td>A new channel published via Channel Manager's \"Publish a new channel\"</td></tr>";
        str += "<tr><td>Channel removals</td><td>A channel removed via Channel Manager's \"Modify a currently published channel\"</td></tr>";
        str += "<tr "
                + clrTag
                + "><td>Channel modifications</td><td>A channel modified via Channel Manager's \"Modify a currently published channel\"</td></tr>";
        str += "<tr><td>Channel added to user's layout</td><td>A channel added to a user's layout via \"preferences\"</td></tr>";
        str += "<tr "
                + clrTag
                + "><td>Channel removed from user's layout</td><td>A channel was removed from a user's layout</td></tr>";
        str += "<tr><td>Channel moved within user's layout</td><td>A channel was moved within a user's layout</td></tr>";
        str += "</table>";
        str += "</div>";
        /*
         *  
         */
        out.print(str);

        out.flush();
    }

    /**
     * @return
     * @throws PortletModeException
     */
    private String getStatsFilterForm(RenderResponse response)
            throws PortletModeException {
        String form = "";

        form += "<form name=\"stats_prefs\" method=\"POST\" action=\""
                + response.createActionURL() + "\">";

        form += "<table cellpadding=2 cellspacing=2 border=0>";
        form += "<tr>";
        form += "<td valign=top align=right><b>Stats from </b></td>";
        form += "<td valign=center>";
        form += getDateDropdown("start", this.startYear, this.startMonth,
                this.startDay, this.startHour, this.startMinute);
        form += " <br /><div align=center><b>to</b></div> ";
        form += getDateDropdown("end", this.endYear, this.endMonth,
                this.endDay, this.endHour, this.endMinute);
        form += "<input type=\"submit\" name=\"stats_prefs_submit\" value=\"show\"><br />";
        form += "</td>";
        form += "</tr>";

        form += "<tr>";
        form += "<td valign=center align=right><b>or</b></td>";
        form += "<td valign=center>";
        form += "<input type=\"submit\" name=\"show_alltime_stats\" value=\"Show All-time Stats\">";
        form += "</td>";
        form += "</tr>";
        form += "</table>";

        form += "</form><br />";

        return form;
    }

    /**
     * Returns an HTML date drop down in the format day/month/year.
     * 
     * @param varPrefix
     *            The variable prefix name to assign to the HTML form element.
     * @param selectedYear
     *            The year to be highlighted by default.
     * @param selectedMonth
     *            The month to be highlighted by default.
     * @param selectedDay
     *            The day to be highlighted by default.
     * @param selectedHour
     *            The hour to be highlighted by default.
     * @param selectedMinute
     *            The minute to be highlighted by default.
     * @return The HTML string.
     */
    private String getDateDropdown(String varPrefix, int selectedYear,
            int selectedMonth, int selectedDay, int selectedHour,
            int selectedMinute) {
        String form = "";
        String selected = "";

        /*
         * YEAR
         */
        form += "<select name=\"" + varPrefix + "_day\">";
        for (int i = 1; i <= 31; i++) {
            selected = i == selectedDay ? " SELECTED" : "";
            form += "<option value=\"" + i + "\"" + selected + ">" + i
                    + "</option>";
        }
        form += "</select>";

        /*
         * MONTH
         */
        String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
                "Aug", "Sep", "Oct", "Nov", "Dec" };
        form += "<select name=\"" + varPrefix + "_month\">";
        for (int i = 1; i <= 12; i++) {
            selected = i == selectedMonth ? " SELECTED" : "";
            form += "<option value=\"" + i + "\"" + selected + ">"
                    + months[i - 1] + "</option>";
        }
        form += "</select>";

        /*
         * DAY
         */
        form += "<select name=\"" + varPrefix + "_year\">";
        for (int i = 2005; i <= 2030; i++) {
            selected = i == selectedYear ? " SELECTED" : "";
            form += "<option value=\"" + i + "\"" + selected + ">" + i
                    + "</option>";
        }
        form += "</select>";

        // Prepare a number format to add leading zeros for hour/minute
        DecimalFormat timeFormat = new DecimalFormat("00");

        /*
         * HOUR
         */
        form += "<select name=\"" + varPrefix + "_hour\">";
        for (int i = 0; i <= 23; i++) {
            selected = i == selectedHour ? " SELECTED" : "";
            form += "<option value=\"" + i + "\"" + selected + ">"
                    + timeFormat.format(i) + "</option>";
        }
        form += "</select>:";

        /*
         * MINUTE
         */
        form += "<select name=\"" + varPrefix + "_minute\">";
        for (int i = 0; i <= 59; i++) {
            selected = i == selectedMinute ? " SELECTED" : "";
            form += "<option value=\"" + i + "\"" + selected + ">"
                    + timeFormat.format(i) + "</option>";
        }
        form += "</select>";

        return form;
    }

    /**
     * Returns the summary statistics in HTML tabular form.
     * 
     * @return The HTML string.
     */
    private String getSummaryStats() {

        this.populateSummaryStatsData(this.getStatsFilter());

        String out = "";
        String clrTag = "bgcolor=\"#EEEEEE\"";

        out += "<div>";
        if (!this.endDateIsDefined() || !this.startDateIsDefined()) {
            out += "<i>Currently displaying all time stats</i><br />";
        }
        out += "<table cellspacing=0 cellpadding=5>";
        out += "<tr><td colspan=2><b>User Stats</b></td></tr>";
        out += "<tr " + clrTag + "><td>Total logins</td><td>"
                + this.numberOfLogins + "</td></tr>";
        out += "<tr><td>Unique users</td><td>" + this.numberOfUniqueUsers
                + "</td></tr>";
        out += "<tr " + clrTag + "><td>Proper logouts</td><td>"
                + this.numberOfLogouts + "</td></tr>";
        out += "<tr><td>Average session length</td><td>"
                + this.averageSessionLength + "</td></tr>";
        out += "<tr><td colspan=2><b>Channel Stats</b></td></tr>";
        out += "<tr " + clrTag + "><td>New channel publishes</td><td>"
                + this.numberOfChannelPublishes + "</td></tr>";
        out += "<tr><td>Channel removals</td><td>"
                + this.numberOfChannelRemovals + "</td></tr>";
        out += "<tr " + clrTag + "><td>Channel modifications</td><td>"
                + this.numberOfChannelModifications + "</td></tr>";
        out += "<tr><td>Channel added to user's layout</td><td>"
                + this.numberOfChannelAddedToLayout + "</td></tr>";
        out += "<tr " + clrTag
                + "><td>Channel removed from user's layout</td><td>"
                + this.numberOfChannelRemovedFromLayout + "</td></tr>";
        out += "<tr><td>Channel moved within user's layout</td><td>"
                + this.numberOfChannelMovedInLayout + "</td></tr>";
        out += "</table>";
        out += "</div>";

        return out;
    }

    /**
     * Queries for the summary statistics based on the specified filter, and
     * populates each statistics data value.
     * 
     * @param f
     *            The query filter to use.
     */
    private void populateSummaryStatsData(StatsQueryFilter f) {
        Connection con = null;
        try {
            con = RDBMServices.getConnection();
            if (con != null) {

                Statement stmt = con.createStatement();

                /*
                 * Total Logins
                 */
                ResultSet rs = stmt.executeQuery(StandardUserStatsQueries
                        .numOfLogins(f));
                while (rs.next()) {
                    this.numberOfLogins = rs.getInt(1);
                }

                /*
                 * Unique Users
                 */
                rs = stmt.executeQuery(StandardUserStatsQueries
                        .numOfUniqueUsers(f));
                while (rs.next()) {
                    this.numberOfUniqueUsers = rs.getInt(1);
                }

                /*
                 * Proper Logouts
                 */
                rs = stmt
                        .executeQuery(StandardUserStatsQueries.numOfLogouts(f));
                while (rs.next()) {
                    this.numberOfLogouts = rs.getInt(1);
                }

                /*
                 * Average Session Length
                 */
                rs = stmt.executeQuery(StandardUserStatsQueries
                        .averageSessionTime(f));
                while (rs.next()) {
                    this.averageSessionLength = rs.getString(1);
                }

                /*
                 * New Channel Publishes
                 */
                rs = stmt.executeQuery(StandardChannelStatsQueries
                        .numOfChannelPublishes(f));
                while (rs.next()) {
                    this.numberOfChannelPublishes = rs.getInt(1);
                }

                /*
                 * Channel Removals
                 */
                System.out.println(StandardChannelStatsQueries
                        .numOfChannelRemovals(f));
                rs = stmt.executeQuery(StandardChannelStatsQueries
                        .numOfChannelRemovals(f));
                while (rs.next()) {
                    this.numberOfChannelRemovals = rs.getInt(1);
                }

                /*
                 * Channel Modifications
                 */
                System.out.println(StandardChannelStatsQueries
                        .numOfChannelDefinitionModifications(f));
                rs = stmt.executeQuery(StandardChannelStatsQueries
                        .numOfChannelDefinitionModifications(f));
                while (rs.next()) {
                    this.numberOfChannelModifications = rs.getInt(1);
                }

                /*
                 * Channel Added To Layout
                 */
                System.out.println(StandardChannelStatsQueries
                        .numOfChannelAddedToLayout(f));
                rs = stmt.executeQuery(StandardChannelStatsQueries
                        .numOfChannelAddedToLayout(f));
                while (rs.next()) {
                    this.numberOfChannelAddedToLayout = rs.getInt(1);
                }

                /*
                 * Channel Removed From Layout
                 */
                System.out.println(StandardChannelStatsQueries
                        .numOfChannelRemovedFromLayout(f));
                rs = stmt.executeQuery(StandardChannelStatsQueries
                        .numOfChannelRemovedFromLayout(f));
                while (rs.next()) {
                    this.numberOfChannelRemovedFromLayout = rs.getInt(1);
                }

                /*
                 * Channel Moved Within Layout
                 */
                System.out.println(StandardChannelStatsQueries
                        .numOfChannelMovedInLayout(f));
                rs = stmt.executeQuery(StandardChannelStatsQueries
                        .numOfChannelMovedInLayout(f));
                while (rs.next()) {
                    this.numberOfChannelMovedInLayout = rs.getInt(1);
                }

            } else {
                System.out.println("ERROR: Cannot connect to db");
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("ERROR: Sql Exception");
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (con != null) {
                RDBMServices.releaseConnection(con);
            }
        }
    }

}