package com.sap.cloud.samples.mailjetmaildemo;
import java.io.BufferedReader;
import java.io.IOException;

import javax.annotation.Resource;
import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
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
 * Servlet implementation class InboundParseServlet
 */
@WebServlet("/inbound")
@MultipartConfig
public class InboundParseServlet extends HttpServlet {
	@Resource(name = "mail/MAILJETSMTP")
    private Session mailSession;
	private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(InboundParseServlet.class);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public InboundParseServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String 	to = request.getParameter("To"),
				from = request.getParameter("From"),
				subject = request.getParameter("Subject"),
				text = request.getParameter("text");

		MailjetClient mailjetClient = MailjetClient.getInstance(getServletContext(), mailSession);
        AbstractMail mail = MailFactory.getMail(MailType.HTTP_MAIL);

		try {
			mail.composeMail(MailjetClient.mailjetProps.getProperty("INBOUND_FROM"),
					MailjetClient.mailjetProps.getProperty("INBOUND_TO"),
					MailjetClient.mailjetProps.getProperty("INBOUND_SUBJECT"),
				    makeInboundMailText(to, from, subject, text));
			mail.send(mailjetClient);
        } catch (Exception e) {
        	e.printStackTrace();
        	LOGGER.error("There was an error when processing email via Sendgrid inbound webhook: " + e.getMessage(), e);
        }
	}

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuilder buffer = new StringBuilder();
		BufferedReader reader = request.getReader();
		String line;
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		JSONObject mailData = new JSONObject(buffer.toString());

		String 	to = mailData.getString("Recipient"),
				from = mailData.getString("From"),
				subject = mailData.getString("Subject"),
				text = mailData.getString("Text-part");

		MailjetClient mailjetClient = MailjetClient.getInstance(getServletContext(), mailSession);
        AbstractMail mail = MailFactory.getMail(MailType.HTTP_MAIL);

		try {
			mail.composeMail(MailjetClient.mailjetProps.getProperty("INBOUND_FROM"),
					MailjetClient.mailjetProps.getProperty("INBOUND_TO"),
					MailjetClient.mailjetProps.getProperty("INBOUND_SUBJECT"),
				    makeInboundMailText(to, from, subject, text));
			mail.send(mailjetClient);
        } catch (Exception e) {
        	e.printStackTrace();
        	LOGGER.error("There was an error when processing email via Sendgrid inbound webhook: " + e.getMessage(), e);
        }
	}

	private String makeInboundMailText(String to, String from, String subject, String text) {
		String returnText = "An email was received via Mailjet Inbound Webhook.\n";
		returnText += "It was automatically categorized as: " + getCategory(subject) + ".\n\n";
		returnText += "Email follows:\n\n";
		returnText += "From: " + from + "\n";
		returnText += "To: " + to + "\n";
		returnText += "Subject: " + subject + "\n\n";
		returnText += "Mail text: " + text + "\n";

		return returnText;
	}

	/**
	 * This is a basic (i.e. very dumb) email categorization method. It will categorize the emails based on the content of its subject.
	 * For demonstration purposes, we're considering the following keywords: "fw:", "support", "error", "question", "ref:" and "order.
	 * @param subject - the subject text
	 * @return a string whose meaning is the category of the email. Possible values are:
	 * "FORWARDED", "SUPPORT REQUEST", "CUSTOMER SERVICE", "SALES ORDER TRACKING" and "GENERAL".
	 */
	private String getCategory(String subject) {
		if (subject.toLowerCase().startsWith("fw:"))
			return "FORWARDED";
		if (subject.toLowerCase().contains("support") || subject.toLowerCase().contains("error"))
			return "SUPPORT REQUEST";
		if (subject.toLowerCase().contains("question"))
			return "CUSTOMER SERVICE";
		if (subject.toLowerCase().startsWith("ref:") || subject.toLowerCase().contains("order"))
			return "SALES ORDER TRACKING";
		return "GENERAL";
	}



}