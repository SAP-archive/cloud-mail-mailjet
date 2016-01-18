package com.sap.cloud.samples.mailjetmaildemo.send;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.mail.internet.AddressException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mailjet.client.MailjetRequest;
import com.mailjet.client.resource.Contact;
import com.mailjet.client.resource.Email;
import com.sap.cloud.samples.mailjetmaildemo.MailException;
import com.sap.cloud.samples.mailjetmaildemo.MailjetClient;
import com.sap.core.connectivity.api.DestinationException;

public class HttpMailWithWrapper extends AbstractMail {
	public HttpMailWithWrapper() {
		super(MailType.HTTP_MAIL_WITH_WRAPPER);
	}

	public String send(MailjetClient mailjetClient) throws MailException {
		try {
			MailjetRequest email = new MailjetRequest(Email.resource)
			                    .property(Email.FROMNAME, getFrom()) // TODO
			                    .property(Email.FROMEMAIL, getFrom())
			                    .property(Email.SUBJECT, getSubject())
			                    .property(Email.TEXTPART, getBody())
			                    .property(Email.RECIPIENTS, getRecipients(getTo()))
			                    .property(Email.MJCUSTOMID, "JAVA-Email");
			String extraURL = MailjetClient.actionSuffix.SEND.suffix();
			return mailjetClient.makeAPIPOSTCall(extraURL, email.getBody());
		} catch (AddressException | URISyntaxException | DestinationException | IOException e) {
	    	throw new MailException("There was an exception sending mail via Mailjet HTTP API: " + e.getMessage());
		}
	}

	private JSONArray getRecipients(ArrayList<String> to) throws AddressException {
		JSONArray recipients = new JSONArray();
		for (String toItem : to) {
			recipients.put(new JSONObject().put(Contact.EMAIL, toItem));
		}
		return recipients;
	}

}
