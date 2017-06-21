package com.accedo.wynkstudio.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * 
 * @author Accedo
 *<p> The custom response for WynkVideo  application.
 * <p>
 * This class contains two attributes, a status code object {@link Status} and a generic data object { @link T}.
 * This is the response object for all the requests made to the application.
 * @param <T>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CPResponse<T> {

	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	
	private Status status;
	private T data;
	
}
