/*
 * BSD License
 *
 * Copyright 2018 AT&T Intellectual Property. All other rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *    and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 * 3. All advertising materials mentioning features or use of this software must display the
 *    following acknowledgement:  This product includes software developed by the AT&T.
 * 4. Neither the name of AT&T nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY AT&T INTELLECTUAL PROPERTY ''AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL AT&T INTELLECTUAL PROPERTY BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

package com.att.cso.opendxl.jython.client;

import java.util.Arrays;
import java.util.List;

/**
 * Message class to hold the fields of a OpenDXL message.  This structure is 
 * used by the Python classes to send the data to the Java callback class that
 * processes the OpenDXL message.
 */
public class DxlMessage {
	public static final int MESSAGE_TYPE_REQUEST = 0;
	public static final int MESSAGE_TYPE_RESPONSE = 1;
	public static final int MESSAGE_TYPE_EVENT = 2;
	public static final int MESSAGE_TYPE_ERROR = 3;
	
	// MQTT message structure
	private String topic = null;
	
	// DXL message structure
	private int messageVersion = 0;
	private int messageType = 0;
	private String messageId = null;
	private String clientId = null;
	private String brokerId = null;
	private List<String> clientIdList = null;
	private List<String> brokerIdList = null;
	private String payload = null;
	private String replyTopic = null;
	private String requestMessageId = null;
	private String serviceId = null;
	private String errorCode = null;
	private String errorMessage = null;
	
	public String getTopic()				{ return topic; }
	public int getMessageVersion() 			{ return messageVersion; }
	public int getMessageType() 			{ return messageType; }
	public String getMessageId() 			{ return messageId; }
	public String getClientId() 			{ return clientId; }
	public String getBrokerId() 			{ return brokerId; }
	public List<String> getClientIdList() 	{ return clientIdList; }
	public List<String> getBrokerIdList() 	{ return brokerIdList; }
	public String getPayload() 				{ return payload; }
	public String getReplyTopic() 			{ return replyTopic; }
	public String getRequestMessageId()		{ return requestMessageId; }
	public String getServiceId() 			{ return serviceId; }
	public String getErrorCode()			{ return errorCode; }
	public String getErrorMessage()			{ return errorMessage; }

	public void setTopic(String topic)							{ this.topic = topic; }
	public void setMessageVersion(int messageVersion) 			{ this.messageVersion = messageVersion; }
	public void setMessageType(int messageType) 				{ this.messageType = messageType; }
	public void setMessageId(String messageId) 					{ this.messageId = messageId; }
	public void setClientId(String clientId) 					{ this.clientId = clientId; }
	public void setBrokerId(String brokerId) 					{ this.brokerId = brokerId; }
	public void setClientIdList(List<String> clientIdList) 		{ this.clientIdList = clientIdList; }
	public void setClientIdList(String[] clientIdList)			{ this.clientIdList = Arrays.asList(clientIdList); }
	public void setBrokerIdList(List<String> brokerIdList) 		{ this.brokerIdList = brokerIdList; }
	public void setBrokerIdList(String[] brokerIdList)			{ this.brokerIdList = Arrays.asList(brokerIdList); }
	public void setPayload(String payload) 						{ this.payload = payload; }
	public void setReplyTopic(String replyTopic) 				{ this.replyTopic = replyTopic; }
	public void setRequestMessageId(String requestMessageId) 	{ this.requestMessageId = requestMessageId; }
	public void setServiceId(String serviceId) 					{ this.serviceId = serviceId; }
	public void setErrorCode(String errorCode)					{ this.errorCode = errorCode; }
	public void setErrorMessage(String errorMessage)			{ this.errorMessage = errorMessage; }

	/**
	 * Create a displayable message for testing
	 *
	 * @return String containing the attributes of the message
	 */
	public String printDxlMessage() {
		
		StringBuilder buf = new StringBuilder();
		buf.append("------------------------------------------------------------\n");
		if (getTopic() != null) {
			buf.append("--------------------------------------------------\n");
			buf.append("   Topic:          " + getTopic() + "\n");
			buf.append("--------------------------------------------------\n");
		}
		buf.append("   Version:        " + getMessageVersion() + "\n");
		buf.append("   Message type:   " + getMessageType() + "\n");
		buf.append("   Message id:     " + getMessageId() + "\n");
		buf.append("   Client id:      " + getClientId() + "\n");
		buf.append("   Broker id:      " + getBrokerId() + "\n");
		buf.append("   Client id list: " + getClientIdList() + "\n");
		buf.append("   Broker id list: " + getBrokerIdList() + "\n");
		buf.append("   Payload:        " + getPayload() + "\n");
		if (getMessageType() == MESSAGE_TYPE_REQUEST) {
			buf.append("   ReplyTo topic:  " + getReplyTopic() + "\n");
			buf.append("   Service id:     " + getServiceId() + "\n");
		} else if (getMessageType() == MESSAGE_TYPE_RESPONSE) {
			buf.append("   Request Msg id: " + getRequestMessageId() + "\n");
			buf.append("   Service id:     " + getServiceId() + "\n");
		} else if (getMessageType() == MESSAGE_TYPE_ERROR) {
			buf.append("   Error code: " + getErrorCode() + "\n");
			buf.append("   Error message: " + getErrorMessage() + "\n");
		}
		
		buf.append("------------------------------------------------------------\n");
		return buf.toString();
	}
	

}
