package com.sap.cloud.samples.mailjetmaildemo.send;

public enum MailType {
	HTTP_MAIL("HTTP mail"),
	HTTP_MAIL_WITH_WRAPPER("HTTP mail with wrapper"),
	SMTP_MAIL("SMTP mail");

	private String name;

	private MailType(String s){
		name = s;
	}

	public String getName(){
		return name;
	}
}
