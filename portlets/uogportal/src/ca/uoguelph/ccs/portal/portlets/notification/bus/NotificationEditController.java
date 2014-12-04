package ca.uoguelph.ccs.portal.portlets.notification.bus;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.BindException;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.mvc.SimpleFormController;
import org.springframework.web.portlet.ModelAndView;

import ca.uoguelph.ccs.portal.UserInfo;
import ca.uoguelph.ccs.portal.portlets.notification.db.UofGTargettedNotificationDAO;

public class NotificationEditController extends SimpleFormController 
{
	private UofGTargettedNotificationDAO notificationDao;

	public NotificationEditController()
	{
		setSessionForm(true);
		setCommandName("notification");
		setCommandClass(Command.class);
		setFormView("tm/notificationEdit");
		setSuccessView("tm/notificationSent");
	}

	public void afterPropertiesSet() throws Exception
	{
	}

	public void onSubmitAction(ActionRequest request, ActionResponse response,
			Object commandObject, BindException errors) throws Exception
	{
		UofGTargettedNotification notification = new UofGTargettedNotification();
		Command command = (Command) commandObject;

		// Copy values from command object into domain object, and fill in
		// defaults.
		Map userInfo = (Map) request.getAttribute(RenderRequest.USER_INFO);
		notification.setOwner(UserInfo.getEmail(userInfo));
		notification.setMessageFrom(UserInfo.getId(userInfo));
		notification.setMessageImportance("normal");
		notification.setRelease(new Timestamp(System.currentTimeMillis()));
                String subject = command.getMessageSubject();
                if (subject == null || subject.trim().length() == 0) {
                    notification.setMessageSubject("[no subject]");
                } else {
                    notification.setMessageSubject(subject);
                }
		notification.setMessage(command.getMessage());
		if (command.getUserId() != null) {
			notification.setUserId(command.getUserId());
		}
		notification.setRole(command.getRole());

		logger.debug("Send Message: " + notification);

		notificationDao.insertTargettedNotification(notification);
	}

	protected Object formBackingObject(PortletRequest request) throws Exception
	{
		Command notification = new Command();
		notification.setCourses(request.getParameter("courses"));
		return notification;
	}

	protected void initBinder(PortletRequest request,
			PortletRequestDataBinder binder) throws Exception
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		binder.registerCustomEditor(Date.class, null, new CustomDateEditor(
				dateFormat, true));
		binder.setAllowedFields(new String[] { "userId", "courses",
				"organizationalStatus", "messageSubject", "message" });
	}

	protected ModelAndView renderInvalidSubmit(RenderRequest request,
			RenderResponse response) throws Exception
	{
		return null;
	}

	protected void handleInvalidSubmit(ActionRequest request,
			ActionResponse response) throws Exception
	{
		response.setRenderParameter("action", "notification");
	}

	public void setNotificationDao(UofGTargettedNotificationDAO notificationDao)
	{
		this.notificationDao = notificationDao;
	}

	/**
	 * Notification command object. The UofGTargettedNotification domain object
	 * has several fields not required by the composer, and does not offer fine
	 * grained access to role attributes (all possible "roles" (ie. userid,
	 * courses, org.status) are collapsed into a single field). This object
	 * adapts the domain object to the UI.
	 * 
	 * @author brharp
	 * 
	 */
	public class Command
	{
		private String userId;

		private String courses;

		private String organizationalStatus;

		private String messageSubject;

		private String message;

		private Integer messageId;

		/**
		 * @return Returns the messageId.
		 */
		public Integer getMessageId()
		{
			return messageId;
		}

		/**
		 * @param messageId
		 *            The messageId to set.
		 */
		public void setMessageId(Integer messageId)
		{
			this.messageId = messageId;
		}

		/**
		 * @return Returns the message.
		 */
		public String getMessage()
		{
			return message;
		}

		/**
		 * @param message
		 *            The message to set.
		 */
		public void setMessage(String message)
		{
			this.message = message;
		}

		/**
		 * @return Returns the messageSubject.
		 */
		public String getMessageSubject()
		{
                    return messageSubject;
		}

		/**
		 * @param messageSubject
		 *            The messageSubject to set.
		 */
		public void setMessageSubject(String messageSubject)
		{
			this.messageSubject = messageSubject;
		}

		/**
		 * @return Returns the courses.
		 */
		public String getCourses()
		{
			return courses;
		}

		/**
		 * @param courses
		 *            The courses to set.
		 */
		public void setCourses(String courses)
		{
			this.courses = courses;
		}

		/**
		 * @return Returns the organizationalStatus.
		 */
		public String getOrganizationalStatus()
		{
			return organizationalStatus;
		}

		/**
		 * @param organizationalStatus
		 *            The organizationalStatus to set.
		 */
		public void setOrganizationalStatus(String organizationalStatus)
		{
			this.organizationalStatus = organizationalStatus;
		}

		/**
		 * @return Returns the userId.
		 */
		public String getUserId()
		{
			return userId;
		}

		/**
		 * @param userId
		 *            The userId to set.
		 */
		public void setUserId(String userId)
		{
			this.userId = userId;
		}

		/**
		 * Synthesizes a "role" string for the UofGTN object.
		 * 
		 * @return role composed of course and org status information.
		 */
		public String getRole()
		{
			boolean first = true;
			StringBuffer role = new StringBuffer();
			if (getCourses() != null) {
				if (!first) {
					role.append(" ");
				}
				role.append(getCourses());
				first = false;
			}
			if (getOrganizationalStatus() != null) {
				if (!first) {
					role.append(" ");
				}
				role.append(getOrganizationalStatus());
				first = false;
			}
			return role.toString();
		}
	}
}
