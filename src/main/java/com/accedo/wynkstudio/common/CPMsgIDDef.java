package com.accedo.wynkstudio.common;

/**
 * 
 * 
 * @author Accedo
 * <p>Bussiness Codes and Messages 
 *
 */

public interface CPMsgIDDef {

	public final static int MSGID_SUCCESS = 200;
	public final static int MSGID_FAILED = 300;
	public final static int MSGID_SERVER_ERROR = 500;
	public final static int MSGID_BAD_REQUEST = 400;
	public final static int MSGID_AUTH_FAILED = 401;
	public final static int MSGID_NOT_FOUND = 404;

	

	public final static String MSG_SUCCESS = "Success";
	public final static String MSG_FAILED = "Failed";
	public final static String MSG_BAD_REQUEST = "The request sent by the client was syntactically incorrect";
	public final static String MSG_NOT_FOUND = "Requested Resource is not found";
	public final static String MSG_SERVER_ERROR = "Server Error";
	public final static String MSG_AUTH_FAILED = "Authentication Failed";
	public final static String WYNKSTUDIO_CP_SERVICE_FAILED = "wynkstudio.cp.service.failed";


}
