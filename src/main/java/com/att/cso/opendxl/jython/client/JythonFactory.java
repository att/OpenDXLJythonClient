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

import java.io.InputStream;
import java.util.Properties;

import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.cso.opendxl.jython.client.interfaces.DxlListenerInterface;
import com.att.cso.opendxl.jython.client.interfaces.DxlProviderInterface;
import com.att.cso.opendxl.jython.client.interfaces.DxlPublisherInterface;
import com.att.cso.opendxl.jython.client.interfaces.DxlRequesterInterface;

/**
 * Factory class to interact with the Jython standalone library that includes
 * the OpenDXL client client code.
 *
 */
public class JythonFactory {
	// Python class to publish events on OpenDXL
	private static final String EVENT_PUBLISHER ="/com/att/cso/opendxl/jython/client/extensions/EventPublisher.py";
	// Python class to call a service on OpenDXL
	private static final String SERVICE_REQUESTER = "/com/att/cso/opendxl/jython/client/extensions/ServiceRequester.py";
	// Python class to listen for events on OpenDXL
	private static final String EVENT_LISTENER = "/com/att/cso/opendxl/jython/client/extensions/EventListener.py";
	// Python class to provide a service on OpenDXL
	private static final String SERVICE_PROVIDER = "/com/att/cso/opendxl/jython/client/extensions/ServiceProvider.py";

	// Interface implemented in the client Python extensions
	public static final String DXL_PUBLISHER_INTERFACE = "com.att.cso.opendxl.jython.client.interfaces.DxlPublisherInterface";
	// Interface implemented in the provider Python extensions
	public static final String DXL_LISTENER_INTERFACE = "com.att.cso.opendxl.jython.client.interfaces.DxlListenerInterface";
	// Interface implemented in the Python extensions
	public static final String DXL_REQUESTER_INTERFACE = "com.att.cso.opendxl.jython.client.interfaces.DxlRequesterInterface";
	// Interface implemented in the provider Python extensions
	public static final String DXL_PROVIDER_INTERFACE = "com.att.cso.opendxl.jython.client.interfaces.DxlProviderInterface";
	// Location to find the OpenDXL client library
	public static final String DEFAULT_JYTHON_LOCATION = "/";
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static JythonFactory instance = null;
	PythonInterpreter interpreter = null;
	
	/**
	 * Get a instance of the Jython factory object
	 * 
	 * @return Instance object of the JythonFactory
	 */
	public static synchronized JythonFactory getInstance() {
		return getInstance(DEFAULT_JYTHON_LOCATION);
	}
	
	/**
	 * Get a instance of the Jython factory object
	 * 
	 * @param location Location of the Jython library
	 * @return Instance object of the JythonFactory
	 */
	public static synchronized JythonFactory getInstance(String location) {
		if (location == null) 
			location = DEFAULT_JYTHON_LOCATION;
		
		if (instance == null) {
			instance = new JythonFactory();
			instance.initJythonObject(location);
		}
		
		return instance;
	}
	
	private JythonFactory() {
	}
	
	/**
	 * Initialization of the Jython interpreter object
	 * 
	 * @param pathToJythonPythonSource Full path to where the OpenDXL client library
	 */
	public void initJythonObject(String pathToJythonPythonSource) {
		
		if (interpreter != null)
			return;
			
		// The pathToJythonPythonSource should be the path to the OpenDXL
		// client library.
		logger.info("Setting python.home to {}", pathToJythonPythonSource);
		
		Properties props = new Properties();
		props.put("python.home", pathToJythonPythonSource);
		// Used to prevent: console: Failed to install '': java.nio.charset.UnsupportedCharsetException: cp0.
		props.put("python.console.encoding", "UTF-8"); 
		//don't respect java accessibility, so that we can access protected members on subclasses
		props.put("python.security.respectJavaAccessibility", "false");

		Properties preprops = System.getProperties();
		
		PythonInterpreter.initialize(preprops, props, new String[0]);
		interpreter = new PythonInterpreter();

	}
	
	/**
	 * Create a resource stream for the resource.  When the Python class is inside the Java jar file
	 * it has to be accessed as a resource stream.
	 * 
	 * @param resource String indicating the package name and file to load 
	 * @return InputStream for the resource sent in
	 */
	private InputStream getResourceAsStream(String resource) {
		return this.getClass().getResourceAsStream(resource);
	}
	
	/**
	 * Simple interface for pre-configured DXL Python publisher interfaces
	 * This method will connect to a DXL client, send a message then
	 * disconnect from the client.
	 * 
	 * @return Object implementing the DxlPublisherInterface
	 */
	public DxlPublisherInterface getDxlPublisherInterface() {
		return (DxlPublisherInterface)getJythonObject(DXL_PUBLISHER_INTERFACE, EVENT_PUBLISHER, DEFAULT_JYTHON_LOCATION);
	}
	
	/**
	 * Simple interface for pre-configured DXL Python listener interfaces
	 * 
	 * @return Object implementing the DxlListenerInterface
	 */
	public DxlListenerInterface getDxlListenerInterface() {
		return (DxlListenerInterface)getJythonObject(DXL_LISTENER_INTERFACE, EVENT_LISTENER, DEFAULT_JYTHON_LOCATION);
	}

	/**
	 * Simple interface for pre-configured DXL Python requester interfaces
	 * 
	 * @return Object implementing the DxlRequesterInterface
	 */
	public DxlRequesterInterface getDxlRequesterInterface() {
		return (DxlRequesterInterface)getJythonObject(DXL_REQUESTER_INTERFACE, SERVICE_REQUESTER, DEFAULT_JYTHON_LOCATION);
	}
	
	/**
	 * Simple interface for pre-configured DXL Python provider interfaces
	 * 
	 * @return Object implementing the DxlProviderInterface
	 */
	public DxlProviderInterface getDxlProviderInterface() {
		return (DxlProviderInterface)getJythonObject(DXL_PROVIDER_INTERFACE, SERVICE_PROVIDER, DEFAULT_JYTHON_LOCATION);
	}

	/**
	 * Interface for DXL Python interfaces that are external to the Java jar file
	 * 
	 * @param interfaceName The Java interface implemented by the Python code
	 * @param pathToJythonModule Full path to the DXL python module implementing the Java interface
	 * @param pathToJythonPythonSource Full path to where the Jython Lib file can be found.  Probably should use DEFAULT_JYTHON_LOCATION
	 * @return Jython object that executes the DXL python module
	 */
	public Object getJythonObject(String interfaceName, String pathToJythonModule, String pathToJythonPythonSource) {
		return getJythonObject(interfaceName, getResourceAsStream(pathToJythonModule), pathToJythonModule, pathToJythonPythonSource);
	}
	
	
	/**
	 * Interface for DXL Python interfaces that are internal to the Java jar file
	 * 
	 * @param interfaceName The Java interface implemented by the Python code
	 * @param jythonModuleStream input stream for the Python class to be executed
	 * @param pathToJythonModule Full path to the DXL python module implementing the Java interface
	 * @param pathToJythonPythonSource Full path to where the Jython Lib file can be found.  Probably should use DEFAULT_JYTHON_LOCATION
	 * @return Jython object that executes the DXL python module
	 */
	public  Object getJythonObject(String interfaceName, InputStream jythonModuleStream, String pathToJythonModule, String pathToJythonPythonSource) {
		Object javaInterface = null;
		
		if (jythonModuleStream == null)
			return null;
				
		interpreter.execfile(jythonModuleStream);
		
		String tempName = pathToJythonModule.substring(pathToJythonModule.lastIndexOf('/') + 1);
		tempName = tempName.substring(0, tempName.indexOf('.'));
		
		String instanceName = tempName.toLowerCase();
		String javaClassName = tempName.substring(0,1).toUpperCase() + tempName.substring(1);
		String objectDef = "=" + javaClassName + "()";
		
		interpreter.exec(instanceName + objectDef);
		
		try {
			Class<?> classInterface = Class.forName(interfaceName);
			javaInterface = interpreter.get(instanceName).__tojava__(classInterface);
		} catch (ClassNotFoundException ex) {
			return null;
		}
		
		return javaInterface;
	}
	
	/**
	 * Close the Python interpreter object effectively shutting down the 
	 * Jython connection.
	 */
	public void closeFactory() {
		if (interpreter == null)  
			return;
		interpreter.cleanup();
		interpreter.close();
		interpreter = null;
	}
}
