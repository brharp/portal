package ca.uoguelph.ccs.portal.portlets.databasestatsreporterportlet;

import ca.uoguelph.ccs.portal.services.stats.*;

/**
 * A collection of standard stats queries about channel related statistics
 * recorded by the database stats recorder.
 * 
 * @author jtey
 *  
 */
public class StandardChannelStatsQueries {

    /**
     * @return
     */
    public static String numOfChannelDefinitionModifications(StatsQueryFilter f) {
        return "SELECT count(1) FROM up_stats_channel_event StatTable, up_user uu WHERE StatTable.user_id=uu.user_id AND StatTable.type_id="
                + StatsEvents.CHANNEL_DEFINITION_MODIFIED + f;
    }

    /**
     * @return
     */
    public static String numOfChannelPublishes(StatsQueryFilter f) {
        return "SELECT count(1) FROM up_stats_channel_event StatTable, up_user uu WHERE StatTable.user_id=uu.user_id AND StatTable.type_id="
                + StatsEvents.CHANNEL_DEFINITION_PUBLISHED + f;
    }

    /**
     * @return
     */
    public static String numOfChannelRemovals(StatsQueryFilter f) {
        return "SELECT count(1) FROM up_stats_channel_event StatTable, up_user uu WHERE StatTable.user_id=uu.user_id AND StatTable.type_id="
                + StatsEvents.CHANNEL_DEFINITION_REMOVED + f;
    }

    /**
     * @return
     */
    public static String numOfChannelAddedToLayout(StatsQueryFilter f) {
        return "SELECT count(1) FROM up_stats_channel_event StatTable, up_user uu WHERE StatTable.user_id=uu.user_id AND StatTable.type_id="
                + StatsEvents.CHANNEL_ADDED_TO_LAYOUT + f;
    }

    /**
     * @return
     */
    public static String numOfChannelRemovedFromLayout(StatsQueryFilter f) {
        return "SELECT count(1) FROM up_stats_channel_event StatTable, up_user uu WHERE StatTable.user_id=uu.user_id AND StatTable.type_id="
                + StatsEvents.CHANNEL_REMOVED_FROM_LAYOUT + f;
    }

    /**
     * @return
     */
    public static String numOfChannelMovedInLayout(StatsQueryFilter f) {
        return "SELECT count(1) FROM up_stats_channel_event StatTable, up_user uu WHERE StatTable.user_id=uu.user_id AND StatTable.type_id="
                + StatsEvents.CHANNEL_MOVED_IN_LAYOUT + f;
    }

}