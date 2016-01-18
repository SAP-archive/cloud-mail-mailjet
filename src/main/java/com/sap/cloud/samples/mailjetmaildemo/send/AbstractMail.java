package com.sap.cloud.samples.mailjetmaildemo.send;

import java.util.ArrayList;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.sap.cloud.samples.mailjetmaildemo.MailException;
import com.sap.cloud.samples.mailjetmaildemo.MailjetClient;

public abstract class AbstractMail {
	private MailType mailType = null;
	private String from, subject, body;
	private ArrayList<String> to, toName;

	public AbstractMail(MailType mailType){
		this.mailType = mailType;
	}

	abstract public String send(MailjetClient mailjetClient) throws MailException;

	public void composeMail(String from, String to, String subject, String body) {
		setFrom(from);
		setTo(to);
		setSubject(subject);
		setBody(body);
	}

	// -----------------
	// getters & setters
	// -----------------
	public MailType getMailType() {
		return mailType;
	}

	public void setMailType(MailType mailType) {
		this.mailType = mailType;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public ArrayList<String> getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = new ArrayList<String>();
		this.toName = new ArrayList<String>();

		InternetAddress[] toAddresses;
		try {
			toAddresses = InternetAddress.parse(to);
			for (int i = 0; i < toAddresses.length; i++) {
				this.to.add(toAddresses[i].getAddress());
				this.toName.add(toAddresses[i].getPersonal() == null ? toAddresses[i].getAddress() : toAddresses[i].getPersonal());
			}
		} catch (AddressException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> getToName() {
		return this.toName;

	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
