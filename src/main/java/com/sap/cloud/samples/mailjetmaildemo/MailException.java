package com.sap.cloud.samples.mailjetmaildemo;

public class MailException extends Exception {
	private static final long serialVersionUID = 1L;

	public MailException() {
		super();
	}

	public MailException(String message) {
		super(message);
	}

	public MailException(String message, Throwable cause) {
		super(message, cause);
	}

	public MailException(Throwable cause) {
		super(cause);
	}
}
