/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.accedo.wynkstudio.exception;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;

/**
 * 
 * @author Accedo
 * <p>
 * Custom Exception for WynkVideo Application
 * 
 */
public class BusinessApplicationException extends DataAccessException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  int msgId;
    private  String message;
    
        
    /**
     * Constructor for custom exception class. Accepts messageid {@link String} , cause of exception{@link Exception}
     * and var-arg tokens {@link String...} and returns new BusinessApplicationException
     * @param msgId
     * @param cause
     * @param tokens
     */
    
    public BusinessApplicationException(int msgId,Exception cause,String... tokens) {
        super("Message-Id[" + msgId + "] Message-Tokens[" + StringUtils.join(tokens,",") + ']',cause);
        this.msgId = msgId;
        this.message = StringUtils.join(tokens,",");
    }
     /**
      * 
      * @param messageID
      * @param tokens
      */
    
    public BusinessApplicationException(int msgId,String... tokens) {
        this(msgId, null, tokens);
    }

    /**
     * 
     * @return messageID
     */
    
    public int getMsgId() {
        return msgId;
    }

    /**
     * 
     * @return messageTokens
     */
    public String getMsgTokens() {
        return message;
    }
}
