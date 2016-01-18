package com.sap.cloud.samples.mailjetmaildemo.send;


public class MailFactory {
	public static AbstractMail getMail(MailType mailType){
	    AbstractMail mail = null;
		switch (mailType) {
		case HTTP_MAIL:
			mail = new HttpMail();
			break;
		case HTTP_MAIL_WITH_WRAPPER:
			mail = new HttpMailWithWrapper();
			break;
		case SMTP_MAIL:
			mail = new SmtpMail();
			break;
		default:
			break;
	    }
		return mail;
	}
}
