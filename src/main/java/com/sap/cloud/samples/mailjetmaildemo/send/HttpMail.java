package com.sap.cloud.samples.mailjetmaildemo.send;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sap.cloud.samples.mailjetmaildemo.MailException;
import com.sap.cloud.samples.mailjetmaildemo.MailjetClient;
import com.sap.core.connectivity.api.DestinationException;

public class HttpMail extends AbstractMail {

	public HttpMail() {
		super(MailType.HTTP_MAIL);
	}

	public String send(MailjetClient mailjetClient) throws MailException {
		try {
			String extraURL = MailjetClient.actionSuffix.SEND.suffix();
			return mailjetClient.makeAPIPOSTCall(extraURL, makeJsonParams());
		} catch (IOException | URISyntaxException | DestinationException e) {
	    	throw new MailException("There was an exception while sending a mail via Mailjet HTTP API: " + e.getMessage());
		}
	}

	private JSONObject makeJsonParams() {
		JSONObject jsonParams = new JSONObject();

		jsonParams.put("FromEmail", getFrom());
		jsonParams.put("FromName", getFrom()); // TODO

		List<Map<String, String>> recList = new ArrayList<>();
		for (int i = 0; i < getTo().size(); i++) {
			Map<String, String> curRec = new HashMap<String, String>();
			curRec.put("Email", getTo().get(i));
			curRec.put("Name", getToName().get(i));
			recList.add(curRec);
		}
		JSONArray recipients = new JSONArray(recList);
		jsonParams.put("Recipients", recipients);

		jsonParams.put("Subject", getSubject());
		jsonParams.put("Text-part", getBody());

		return jsonParams;
	}

}
