package com.att.cso.opendxl.jython.client.exceptions;

/**
 * Custom DXL exception used to pass errors back from the Python code
 * to the Java code.
 *
 */
public class DxlJythonException extends Exception {
	private static final long serialVersionUID = 1L;

	/** numerical error code for the message */
	private final int errorCode;
	/** textual message describing the error */
	private final String errorMessage;
	
	public DxlJythonException(int error, String message) {
		this.errorCode = error;
		this.errorMessage = message;
	}
	
	public int getErrorCode() 	{ return errorCode; }
	@Override
	public String getMessage() 	{ return errorMessage; }
}
