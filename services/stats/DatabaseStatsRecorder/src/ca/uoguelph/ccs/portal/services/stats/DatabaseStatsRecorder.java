package ca.uoguelph.ccs.portal.services.stats;

import org.jasig.portal.services.stats.IStatsRecorder;
import org.jasig.portal.ChannelDefinition;
import org.jasig.portal.UserProfile;
import org.jasig.portal.layout.IUserLayoutChannelDescription;
import org.jasig.portal.layout.IUserLayoutFolderDescription;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.RDBMServices;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;

/**
 * Formulates stats messages which can be logged, printed, etc.
 */
public class DatabaseStatsRecorder implements IStatsRecorder {

    /**
     * User Id to represent the ficticious guest user
     */
    public static final int GUEST_USER_ID = -1;

    /**
     * User Id to represent the ficticious null user
     */
    public static final int NULL_USER_ID = -99;

    private static final Log log = LogFactory
            .getLog(DatabaseStatsRecorder.class);

    public void recordLogin(IPerson person) {
        insertUserEventStatsRecord(StatsEvents.LOGIN, person);
    }

    public void recordLogout(IPerson person) {
        insertUserEventStatsRecord(StatsEvents.LOGOUT, person);
    }

    public void recordSessionCreated(IPerson person) {
        insertUserEventStatsRecord(StatsEvents.SESSION_CREATED, person);
    }

    public void recordSessionDestroyed(IPerson person) {
        insertUserEventStatsRecord(StatsEvents.SESSION_DESTROYED, person);
    }

    public void recordChannelDefinitionPublished(IPerson person,
            ChannelDefinition channelDef) {
        insertChannelEventStatsRecord(StatsEvents.CHANNEL_DEFINITION_PUBLISHED,
                person, channelDef);
    }

    public void recordChannelDefinitionModified(IPerson person,
            ChannelDefinition channelDef) {
        insertChannelEventStatsRecord(StatsEvents.CHANNEL_DEFINITION_MODIFIED,
                person, channelDef);
    }

    public void recordChannelDefinitionRemoved(IPerson person,
            ChannelDefinition channelDef) {
        insertChannelEventStatsRecord(StatsEvents.CHANNEL_DEFINITION_REMOVED,
                person, channelDef);
    }

    public void recordChannelAddedToLayout(IPerson person, UserProfile profile,
            IUserLayoutChannelDescription channelDesc) {
        insertChannelEventStatsRecord(StatsEvents.CHANNEL_ADDED_TO_LAYOUT,
                person, channelDesc);
    }

    public void recordChannelUpdatedInLayout(IPerson person,
            UserProfile profile, IUserLayoutChannelDescription channelDesc) {
        insertChannelEventStatsRecord(StatsEvents.CHANNEL_UPDATED_IN_LAYOUT,
                person, channelDesc);
    }

    public void recordChannelMovedInLayout(IPerson person, UserProfile profile,
            IUserLayoutChannelDescription channelDesc) {
        insertChannelEventStatsRecord(StatsEvents.CHANNEL_MOVED_IN_LAYOUT,
                person, channelDesc);
    }

    public void recordChannelRemovedFromLayout(IPerson person,
            UserProfile profile, IUserLayoutChannelDescription channelDesc) {
        insertChannelEventStatsRecord(StatsEvents.CHANNEL_REMOVED_FROM_LAYOUT,
                person, channelDesc);
    }

    public void recordFolderAddedToLayout(IPerson person, UserProfile profile,
            IUserLayoutFolderDescription folderDesc) {
        insertFolderEventStatsRecord(StatsEvents.FOLDER_ADDED_TO_LAYOUT, person,
                folderDesc);
    }

    public void recordFolderUpdatedInLayout(IPerson person,
            UserProfile profile, IUserLayoutFolderDescription folderDesc) {
        insertFolderEventStatsRecord(StatsEvents.FOLDER_UPDATED_IN_LAYOUT, person,
                folderDesc);
    }

    public void recordFolderMovedInLayout(IPerson person, UserProfile profile,
            IUserLayoutFolderDescription folderDesc) {
        insertFolderEventStatsRecord(StatsEvents.FOLDER_MOVED_IN_LAYOUT, person,
                folderDesc);
    }

    public void recordFolderRemovedFromLayout(IPerson person,
            UserProfile profile, IUserLayoutFolderDescription folderDesc) {
        insertFolderEventStatsRecord(StatsEvents.FOLDER_REMOVED_FROM_LAYOUT, person,
                folderDesc);
    }

    public void recordChannelInstantiated(IPerson person, UserProfile profile,
            IUserLayoutChannelDescription channelDesc) {
        insertChannelEventStatsRecord(StatsEvents.CHANNEL_INSTANTIATED, person,
                channelDesc);
    }

    public void recordChannelRendered(IPerson person, UserProfile profile,
            IUserLayoutChannelDescription channelDesc) {
        insertChannelEventStatsRecord(StatsEvents.CHANNEL_RENDERED, person,
                channelDesc);
    }

    public void recordChannelTargeted(IPerson person, UserProfile profile,
            IUserLayoutChannelDescription channelDesc) {
        insertChannelEventStatsRecord(StatsEvents.CHANNEL_TARGETED, person,
                channelDesc);
    }

    /**
     * Inserts the specified channel event involving the specified person and
     * channel description into the database.
     * 
     * @param channelEventType
     *            The channel event type.
     * @param person
     *            The person involved.
     * @param channelDesc
     *            The Channel Description object of the channel involved.
     */
    private void insertChannelEventStatsRecord(int channelEventType,
            IPerson person, IUserLayoutChannelDescription channelDesc) {
        /*
         * Retrieve the Channel ID of the channel involved. Note: In an
         * IUserLayoutChannelDescription object, the channel id is returned by
         * getChannelPublishId(), which is not what you expect at first.
         */
        String channelID = channelDesc.getChannelPublishId();
        this.insertChannelEventStatsRecord(channelEventType, person, Integer
                .parseInt(channelID));
    }

    /**
     * Inserts the specified channel event involving the specified person and
     * channel definition into the database.
     * 
     * @param channelEventType
     *            The channel event type.
     * @param person
     *            The person involved.
     * @param channelDef
     *            The Channel Definition object of the channel involved.
     */
    private void insertChannelEventStatsRecord(int channelEventType,
            IPerson person, ChannelDefinition channelDef) {
        this.insertChannelEventStatsRecord(channelEventType, person, channelDef
                .getId());
    }

    /**
     * Inserts the specified channel event involving the specified person and
     * channel into the database.
     * 
     * @param channelEventType
     *            The channel event type.
     * @param person
     *            The person involved.
     * @param channelID
     *            The channel id.
     */
    private void insertChannelEventStatsRecord(int channelEventType,
            IPerson person, int channelID) {

        String sql = "";
        sql += "insert into up_stats_channel_event values(";
        sql += "'" + channelEventType + "',";
        sql += "'" + getUserId(person) + "',";
        sql += "'" + channelID + "',";
        sql += "'" + PortalHost.getHostId() + "',";
        sql += "NOW()";
        sql += ")";

        insertToDatabase(sql);

    }

    /**
     * Inserts the specified user event involving the specified person into the
     * database.
     * 
     * @param userEventType
     *            The user event type.
     * @param person
     *            The person involved.
     *  
     */
    private void insertUserEventStatsRecord(int userEventType, IPerson person) {

        String sql = "";
        sql += "insert into up_stats_user_event values(";
        sql += "'" + userEventType + "',";
        sql += "'" + getUserId(person) + "',";
        sql += "'" + PortalHost.getHostId() + "',";
        sql += "NOW()";
        sql += ")";

        insertToDatabase(sql);

    }

    /**
     * Inserts the specified folder or "container" (e.g., new tabs/columns)
     * event involving the specified person into the database.
     * 
     * @param eventType
     *            The folder event type.
     * @param person
     *            The person involved.
     * @param folderDesc
     *            The folder description object.
     */
    private void insertFolderEventStatsRecord(int eventType, IPerson person,
            IUserLayoutFolderDescription folderDesc) {

        /*
         * String folderType = "unknown"; switch (folderDesc.getType()) { case
         * IUserLayoutFolderDescription.FOOTER_TYPE: folderType = "footer";
         * break;
         * 
         * case IUserLayoutFolderDescription.HEADER_TYPE: folderType = "header";
         * break;
         * 
         * case IUserLayoutFolderDescription.REGULAR_TYPE: folderType =
         * "regular"; break; }
         */

        String sql = "";
        sql += "insert into up_stats_folder_event values(";
        sql += "'" + eventType + "',";
        sql += "'" + getUserId(person) + "',";
        sql += "'" + folderDesc.getId() + "',";
        sql += "'" + folderDesc.getName() + "',";
        sql += "'" + PortalHost.getHostId() + "',";
        sql += "NOW()";
        sql += ")";

        insertToDatabase(sql);

    }

    /**
     * Performs an insert to the database based on the specified SQL insert
     * statement.
     * 
     * @param sql
     *            The insert statement to execute.
     */
    public void insertToDatabase(String sql) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            if (conn != null) {
                stmt = conn.createStatement();
                stmt.executeUpdate(sql);

                if (stmt != null) {
                    stmt.close();
                }
            }
        } catch (SQLException sqle) {
            log.error("ERROR: SQL statement (" + sql
                    + ") failed to insert DatabaseStatsRecorder record ", sqle);
        } finally {
            releaseConnection(conn);
        }
    }

    /**
     * Returns the Portal Database <code>Connection</code> instance.
     * 
     * @return The Portal DB connection.
     */
    public static Connection getConnection() {
        Connection conn = null;

        conn = RDBMServices.getConnection("PortalDb"); //PortalDB is a JNDI
        // handle to the portal
        // DB that is used to get
        // the userid of the
        // user.

        if (conn == null) {
            log
                    .error("ERROR: connection to PortalDB could not be established.");
        }
        return conn;
    }

    /**
     * Releases the connection to the database.
     * 
     * @param conn
     */
    public static void releaseConnection(Connection conn) {
        RDBMServices.releaseConnection(conn);
    }

    /**
     * Retrieves the user id of the specified person.
     * 
     * @param person
     *            the IPerson of the person.
     * @return The user id.
     */
    private int getUserId(IPerson person) {
        int id = NULL_USER_ID;

        if (person != null) {
            if (person.isGuest()) {
                id = GUEST_USER_ID;
            } else {
                id = person.getID();
            }
        }

        return id;
    }

}

