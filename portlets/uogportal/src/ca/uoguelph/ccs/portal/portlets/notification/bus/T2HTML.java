package ca.uoguelph.ccs.portal.portlets.notification.bus;
import java.lang.String;

/**
 * 
 * @author lalit
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class T2HTML {
	private final static String ANCHOR = "a";
	private final static String START_TAG = "<";
	private final static String END_TAG = ">";
	private final static String DQUOTE = "\"";
	
	private final static String HREF_DELIMITER = "href";
	private final static String HTTP_DELIMITER = "http://";
	private final static String HTTP_REG_EX = "[hH][tT][tT][pP]://";
	
	private final static String TARGET = "target=\"tmessage\"";
	private static final String NEW_LINE = System.getProperty("line.separator");
	private static final String ONCLICK = "onclick=\"window.open('','tmessage','resizable=yes,location=no,height=600,width=800,toolbar=yes,scrollbars=yes')\"";
	
	/**
	 * 
	 * @param payload
	 * @return
	 */
	public static String convertText2HTML(String payload) {
		String sneak = //that's a exact copy of the message from my yahoo.com e-mail account
			"text/plain\n" +
			"I would like to invite all University of Guelph faculty and staff to \n" +
			"the third \n" +
			"annual Community Breakfast to start the new academic year. \n\n" +
			"The Community Breakfast will be held on Thursday, September 8, 2005 \n" +
			"from \n" +
			"8:30 a.m. to 10:00 a.m. in the Gryphon Dome. \n\n" +
			"The Community Breakfast will be an opportunity not only to celebrate \n" +
			"the \n" +
			"start of the academic year but also the achievements of staff and \n" +
			"faculty \n" +
			"who make significant contributions to the life of the University. \n" +
			"Special \n" +
			"awards are:\n\n" +
			"*   25 years of Service with the University\n" +
			"*   President's Awards for Exemplary Staff Service\n" +
			"*   Undergraduate Academic Advising Medallion\n" +
			"*   UGAA Employee Volunteer Award\n\n" +
			"Deans, directors, chairs and supervisors will be asked to provide time \n" +
			"for as \n" +
			"many staff as possible to attend. I hope that you will be able to \n" +
			"attend. \n\n" +                    
			"Please R.S.V.P. to June Pearson at ext. 53093, or on-line at http://\n" +
			"www.uoguelph.ca/president/breakfastrsvp/ by September 2, 2005.\n\n" +
			"If you have already responded to an earlier invitation to this event,\n" + 
			"thank \n" +
			"you and please disregard this notice.\n\n" +
			"Alastair J. S. Summerlee\n" +
			"President and Vice-Chancellor\n\n\n" +
			"This e-mail is sent out in accordance with the University of Guelph \n" +
			"Mass Electronic Mail Policy available at: \n" +
			"http://www.uoguelph.ca/info/massemail\n";
		//payload=sneak;
		
		String lines[] = payload.split("\n"); //split on new line
		if (lines == null || lines.length == 0) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<div style=\"overflow:auto;\">");
		for (int i=0; i<lines.length; i++) {
			if (i==0) { //magic line: content type
				continue;
			}
			String line = lines[i].trim();
			String[] href = line.split(HTTP_REG_EX);
			
			if (href == null || href.length < 2) {
				sb.append(line).append("<br />\n");
			} else {
				
				for (int j=0; j<href.length; j++) {
					if (j == 0) {
						if (href[j] == null || href[j].length() == 0 || href[j].equals("")) {
						} else {
							sb.append(href[j]);
						}
						continue;
					}
					
					sb.append(" ").append(START_TAG+ANCHOR).append(" ").
					append(TARGET).append(" ").append(ONCLICK).append(" ").
					append(HREF_DELIMITER).append("=").append(DQUOTE).
					append(HTTP_DELIMITER); 
					//the tag string is the link
					//split on white space to get the http tag string 
					String tokens[] = href[j].split("\\s+");
					
					if (tokens[0].endsWith(".")) {
						int len = tokens[0].length();
						tokens[0] = tokens[0].substring(0,len-1);
						sb.append(tokens[0]).append(DQUOTE+END_TAG).append(HTTP_DELIMITER+tokens[0]).
						append(START_TAG+"/"+ANCHOR+END_TAG).append(".");
					} else {
						sb.append(tokens[0]).append(DQUOTE+END_TAG).append(HTTP_DELIMITER+tokens[0]).
						append(START_TAG+"/"+ANCHOR+END_TAG);
					}
					if (tokens.length>1) {
						sb.append(" ").append(tokens[1]);
					}
					if (j == (href.length-1)) {                     
						sb.append("<br />\n"); 
					}
				}
			}
		}
		sb.append("\n</div>");
		//System.out.println("\n" + sb);
		return sb.toString();
	}
	public static void main (String[] args) {
		convertText2HTML("who");
	}
}