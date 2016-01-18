package com.sap.cloud.samples.mailjetmaildemo;
import java.io.BufferedReader;
import java.io.IOException;

import javax.annotation.Resource;
import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cloud.samples.mailjetmaildemo.send.AbstractMail;
import com.sap.cloud.samples.mailjetmaildemo.send.MailFactory;
import com.sap.cloud.samples.mailjetmaildemo.send.MailType;

/**
 * Servlet implementation class MailEventServlet
 */
@WebServlet("/mailevent")
public class EventServlet extends HttpServlet {
	@Resource(name = "mail/MAILJETSMTP")
    private Session mailSession;
	private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(EventServlet.class);

    public EventServlet() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MailjetClient mailjetClient = MailjetClient.getInstance(getServletContext(), mailSession);
        AbstractMail mail = MailFactory.getMail(MailType.HTTP_MAIL);

		try {
	       	mail.composeMail(MailjetClient.mailjetProps.getProperty("EMAIL_EVENT_FROM"),
	       			MailjetClient.mailjetProps.getProperty("EMAIL_EVENT_TO"),
	       			MailjetClient.mailjetProps.getProperty("EMAIL_EVENT_SUBJECT"),
					makeInboundMailText(getBodyContent(request)));
			String sendResult = mail.send(mailjetClient);
			if (sendResult.contains("Sent")) {
				LOGGER.info("An email was been sent triggered by an Mailjet email event.");
			} else {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				LOGGER.error("There was an error when processing email via Mailjet email event.");
			}
		} catch (MailException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LOGGER.error("There was an error when processing email via Mailjet email event.");
		}
	}

	private JSONObject getBodyContent(HttpServletRequest request) {
		StringBuilder content = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = request.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}
			reader.close();
		} catch (IOException e) {
			LOGGER.error("There was an error when trying to read the request body: " + e.getMessage(), e);
		}

		JSONObject jsonObj = new JSONObject(content.toString());

		return jsonObj;
	}

	private String makeInboundMailText(JSONObject currentEvent) {
		StringBuilder returnText = new StringBuilder();

		returnText.append("The following emails had problems being delivered:\n\n");

		returnText.append("Emails sent to ");
		returnText.append(currentEvent.getString("email"));
		returnText.append(" could not be delivered.\n");
		returnText.append("Reason: ");
		returnText.append(currentEvent.getString("error"));
		returnText.append(".\n\n");

		return returnText.toString();
	}

}
