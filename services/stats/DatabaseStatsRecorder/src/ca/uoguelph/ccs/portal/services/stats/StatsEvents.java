/*
 * Created on 16-Nov-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ca.uoguelph.ccs.portal.services.stats;

/**
 * @author jtey
 *  
 */
public class StatsEvents {

    public static final int LOGIN = 1;

    public static final int LOGOUT = 2;

    public static final int SESSION_CREATED = 3;

    public static final int SESSION_DESTROYED = 4;

    public static final int CHANNEL_DEFINITION_PUBLISHED = 1;

    public static final int CHANNEL_DEFINITION_MODIFIED = 2;

    public static final int CHANNEL_DEFINITION_REMOVED = 3;

    public static final int CHANNEL_ADDED_TO_LAYOUT = 4;

    public static final int CHANNEL_UPDATED_IN_LAYOUT = 5;

    public static final int CHANNEL_MOVED_IN_LAYOUT = 6;

    public static final int CHANNEL_REMOVED_FROM_LAYOUT = 7;

    public static final int CHANNEL_INSTANTIATED = 8;

    public static final int CHANNEL_RENDERED = 9;

    public static final int CHANNEL_TARGETED = 10;

    public static final int FOLDER_ADDED_TO_LAYOUT = 1;

    public static final int FOLDER_UPDATED_IN_LAYOUT = 2;

    public static final int FOLDER_MOVED_IN_LAYOUT = 3;

    public static final int FOLDER_REMOVED_FROM_LAYOUT = 4;

    /**
     * Retrieves the action statement string for a specified stats event and
     * context, e.g., "Channel was added to layout". The context parameter is
     * omitted here.
     * 
     * @param actionType
     *            The type of action.
     * @return A string which describes the specified action in plain english.
     */
    public static String getActionStatement(int actionType) {
        return StatsEvents.getActionStatement(actionType, "");
    }

    /**
     * Retrieves the action statement string for a specified stats event and
     * context, e.g., "Channel bookmarks was added to layout".
     * 
     * @param actionType
     *            The uPortal action type, as defined in StatsEvents.
     * @param context
     *            The context or subject of the specified action, e.g.,
     *            "bookmarks" is the context for the example above.
     * @return A string which describes the specified action in plain english.
     */
    public static String getActionStatement(int actionType, String context) {
        String s = "";
        context = context.length() == 0 ? "" : context + " ";

/*        switch (actionType) {

        case StatsEvents.LOGIN:
            s = "Login";
            break;

        case StatsEvents.LOGOUT:
            s = "Login";
            break;

        case StatsEvents.SESSION_CREATED:
            s = "Session created";
            break;

        case StatsEvents.SESSION_DESTROYED:
            s = "Session destroyed";
            break;

        case StatsEvents.CHANNEL_DEFINITION_PUBLISHED:
            s = "Channel " + context + "was published";
            break;

        case StatsEvents.CHANNEL_DEFINITION_MODIFIED:
            s = "Channel " + context + "was modified";
            break;

        case StatsEvents.CHANNEL_DEFINITION_REMOVED:
            s = "Channel " + context + "was removed";
            break;

        case StatsEvents.CHANNEL_ADDED_TO_LAYOUT:
            s = "Channel " + context + "was added to layout";
            break;

        case StatsEvents.CHANNEL_UPDATED_IN_LAYOUT:
            s = "Channel " + context + "was updated in layout";
            break;

        case StatsEvents.CHANNEL_MOVED_IN_LAYOUT:
            s = "Channel " + context + "was moved in layout";
            break;

        case StatsEvents.CHANNEL_REMOVED_FROM_LAYOUT:
            s = "Channel " + context + "was removed from layout";
            break;

        case StatsEvents.FOLDER_ADDED_TO_LAYOUT:
            s = "Folder " + context + "was added to layout";
            break;

        case StatsEvents.FOLDER_UPDATED_IN_LAYOUT:
            s = "Folder " + context + "was updated in layout";
            break;

        case StatsEvents.FOLDER_MOVED_IN_LAYOUT:
            s = "Folder " + context + "was moved in layout";
            break;

        case StatsEvents.FOLDER_REMOVED_FROM_LAYOUT:
            s = "Folder " + context + "was removed from layout";
            break;

        case StatsEvents.CHANNEL_INSTANTIATED:
            s = "Channel " + context + "was instantiated";
            break;

        case StatsEvents.CHANNEL_RENDERED:
            s = "Channel " + context + "was rendered";
            break;

        case StatsEvents.CHANNEL_TARGETED:
            s = "Channel " + context + "was targetted";
            break;

        }*/

        return s;
    }

}