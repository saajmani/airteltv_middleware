package com.accedo.wynkstudio.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * 
 * @author Accedo
 * <p> Custom ResponseStatus 
 * Contains two attributes code {@link int} and message {@link String}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseStatus {
	
	private int code;
	private String message;
	/**
	 * Constructor for new ResponseStatus object
	 * @param code     {@link int}
	 * @param message  {@link String}
	 */
	private ResponseStatus(int code, String message)
    {        
        this.code = code;
        this.message = message;
    }	
	/**
	 * getter method for code
	 * @return
	 */
	public int getCode()
    {
        return code;
    }
	/**
	 * getter method for message
	 * @return
	 */
	public String getMessage(){
		return message;
	}
	
	
	public static ResponseStatus[] values()
    {
        return VALUES.clone();
    }
	/**
	 * Accepts response code and returns ResponseStatus object 
	 * @param code
	 * @return response {@link ResponseStatus}
	 */
	public static ResponseStatus fromCode(int code){
		for (ResponseStatus responseStatus : VALUES) {
			if(responseStatus.code == code){
				return responseStatus;
			}
		}
		return BAD_REQUEST;
	}

//    public static ResponseStatus valueOf(String name)
//    {
//        return ResponseStatus.valueOf(name);
//    }
//    
    public static final ResponseStatus SUCCESS;
    public static final ResponseStatus BAD_REQUEST;
    public static final ResponseStatus AUTH_FAILED;
    public static final ResponseStatus INVALID_MSISDN;
    public static final ResponseStatus FAILED;
    public static final ResponseStatus SERVER_ERROR;
    private static final ResponseStatus VALUES[];

    static 
    {
        SUCCESS = new ResponseStatus(CPMsgIDDef.MSGID_SUCCESS, CPMsgIDDef.MSG_SUCCESS);
        BAD_REQUEST = new ResponseStatus(CPMsgIDDef.MSGID_BAD_REQUEST, CPMsgIDDef.MSG_BAD_REQUEST);
        AUTH_FAILED = new ResponseStatus( CPMsgIDDef.MSGID_AUTH_FAILED, CPMsgIDDef.MSG_AUTH_FAILED);
        INVALID_MSISDN = new ResponseStatus( CPMsgIDDef.MSGID_NOT_FOUND, CPMsgIDDef.MSG_NOT_FOUND);
        FAILED = new ResponseStatus( CPMsgIDDef.MSGID_FAILED, CPMsgIDDef.MSG_FAILED);
        SERVER_ERROR = new ResponseStatus(CPMsgIDDef.MSGID_SERVER_ERROR, CPMsgIDDef.MSG_SERVER_ERROR);
        VALUES = (new ResponseStatus[] {
            SUCCESS, BAD_REQUEST, AUTH_FAILED, INVALID_MSISDN, FAILED, SERVER_ERROR
        });
    }

	public void setMessage(String message) {
		this.message = message;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
    
}
