package com.github.soulaway.journals.service;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = RuntimeException.class.getName().hashCode();

	public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
