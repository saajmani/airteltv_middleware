package com.accedo.wynkstudio.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * 
 * @author Accedo
 * <p> Custom Status 
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {
	
	private int code;
	
	private String message;
	/**
	 * getter method for code
	 * @return code
	 */
	public int getCode() {
		return code;
	}
	/**
	 * setter method for code
	 * @param code
	 */
	public void setCode(int code) {
		this.code = code;
	}
	/**
	 * getter method for message
	 * @return message 
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * setter method for message
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * constructs {@link Status} from ResponseStatus
	 * @param status
	 */
	public Status(ResponseStatus status)
    {
        code = status.getCode();
        message = status.getMessage();
    }
	
	public Status()
    {
    }
}
