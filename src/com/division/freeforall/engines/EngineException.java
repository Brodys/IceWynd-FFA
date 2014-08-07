package com.division.freeforall.engines;

public class EngineException extends Exception {

	private static final long serialVersionUID = 1L;

	public EngineException(final String message) {
		super(message);
	}

	public EngineException(final String message, final Throwable throwable) {
		super(message, throwable);
	}
}
