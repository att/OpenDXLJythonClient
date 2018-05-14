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

package com.att.cso.opendxl.jython.client.interfaces;

import com.att.cso.opendxl.jython.client.exceptions.DxlJythonException;

/**
 * Simple interface to bridge Java and Python modules to listen for messages 
 * from DXL. Python modules should import this interface. i.e.
 *    from com.att.cso.opendxl.jython.client.interfaces import DxlProviderInterface
 * Should be at the top of your python source file.
 */
public interface DxlListenerInterface {

	/** 
	 * Provider start method to run the event listener, method does not
	 * return until the listener is stopped.
	 * 
	 * @param configFile location of the dxlclient.config file
	 * @param topic topic name ("/my/service/foo/bar")
	 * @param dxlCallback java callback implementation to process message payload
	 * @return message that indicates reason for exiting the implementer
	 * @throws DxlJythonException Thrown when unable to create a connection and start a listener
	 */
	public String start(String configFile, String topic, DxlCallbackInterface dxlCallback) throws DxlJythonException;
	
	/**
	 * Stop the listener and destroy the connection to the DXL fabric
	 */
	public void stop();
}
