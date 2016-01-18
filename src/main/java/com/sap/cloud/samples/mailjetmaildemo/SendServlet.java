package com.sap.cloud.samples.mailjetmaildemo;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.cloud.samples.mailjetmaildemo.send.AbstractMail;
import com.sap.cloud.samples.mailjetmaildemo.send.MailFactory;
import com.sap.cloud.samples.mailjetmaildemo.send.MailType;

/**
 * Servlet implementation class HttpServlet
 */
@WebServlet("/mailjet")
public class SendServlet extends HttpServlet {
	@Resource(name = "mail/MAILJETSMTP")
    private Session mailSession;
	private static final long serialVersionUID = 1L;

    /**
     * @see javax.servlet.http.HttpServlet#javax.servlet.http.HttpServlet()
     */
    public SendServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see SendServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String action = request.getParameter("action");

		String from = request.getParameter("fromaddress");
        String to = request.getParameter("toaddress");
        String subjectText = request.getParameter("subjecttext");
        String mailText = request.getParameter("mailtext");

        AbstractMail mail = null;
		if (action.equals("sendHttpMail")) {
			mail = MailFactory.getMail(MailType.HTTP_MAIL);
		} else if (action.equals("sendHttpMailWithWrapper")) {
			mail = MailFactory.getMail(MailType.HTTP_MAIL_WITH_WRAPPER);
		} else if (action.equals("sendSmtpMail")) {
			mail = MailFactory.getMail(MailType.SMTP_MAIL);
		} else {
			out.println("No action selected");
			return;
		}

		MailjetClient mailjetClient = MailjetClient.getInstance(getServletContext(), mailSession);

		try {
			mail.composeMail(from, to, subjectText, mailText);
			String sendResult = mail.send(mailjetClient);
			if (sendResult.contains("Sent")) {
				out.println("The email has been sent via Mailjet using the " + mail.getMailType().getName() + ".");
				out.println(sendResult);
			} else {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				out.println("Something has gone wrong while trying to send a Mailjet " + mail.getMailType().getName() + ", please check the logs...");
				out.println(sendResult);
			}
		} catch (MailException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.println(e.getMessage());
		}
	}

}
