package com.sap.cloud.samples.mailjetmaildemo.send;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cloud.samples.mailjetmaildemo.MailException;
import com.sap.cloud.samples.mailjetmaildemo.MailjetClient;

public class SmtpMail extends AbstractMail{
	private MimeMessage mimeMessage;
	private Transport transport = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(SmtpMail.class);

    public SmtpMail() {
    	super(MailType.SMTP_MAIL);
    }

    public String send(MailjetClient mailjetClient) throws MailException {
    	mimeMessage = new MimeMessage(MailjetClient.mailSession);
    	try {
	    	InternetAddress[] fromAddress = InternetAddress.parse(this.getFrom());
	        InternetAddress[] toAddresses = this.buildToAddresses();

	    	mimeMessage.setFrom(fromAddress[0]);
	        mimeMessage.setRecipients(RecipientType.TO, toAddresses);
	        mimeMessage.setSubject(this.getSubject(), "UTF-8");
	        MimeMultipart multiPart = new MimeMultipart("alternative");
	        MimeBodyPart part = new MimeBodyPart();
	        part.setText(this.getBody(), "utf-8", "plain");
	        multiPart.addBodyPart(part);
	        mimeMessage.setContent(multiPart);
    	} catch (Exception e) {
    		LOGGER.error("There was an error when initializing the mail message:");
    		LOGGER.error(e.getMessage());
    	}
    	try {
	        transport = MailjetClient.mailSession.getTransport();
	    	transport.connect();
	        transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
	        return "Sent"; // TODO send information equivalent to the HTTP return
    	} catch (MessagingException e) {
    		LOGGER.error("Sending mail with SMTP via Mailjet API failed!", e);
            throw new MailException("There was an exception while sending a mail via Mailjet SMTP: " + e);
    	} finally {
            // Close transport layer
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    throw new MailException(e);
                }
            }
        }
    }

    public void setHeader(String key, String value) {
    	try {
			mimeMessage.setHeader(key, value);
		} catch (MessagingException e) {
			LOGGER.error("Error when setting the header: " + key + " = " + value);
			e.printStackTrace();
		}
    }

    public MimeMessage getMimeMessage() {
    	return mimeMessage;
    }

    public Transport getTransport() {
    	return transport;
    }

    private InternetAddress[] buildToAddresses() throws AddressException {
        StringBuilder builder = new StringBuilder();
        String separator = "", realSeparator = ",";
        for(String toAddress: this.getTo()) {
        	builder.append(separator).append(toAddress);
            separator = realSeparator;
        }
         return InternetAddress.parse(builder.toString());
    }
}
