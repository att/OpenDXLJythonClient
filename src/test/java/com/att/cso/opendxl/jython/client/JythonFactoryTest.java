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

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test case place holder.  This software relies on a working OpenDXL broker
 * or fabric.  Because of this, automated test cases are not really a 
 * achievable goal.  This code has been left to allow users to have a 
 * framework for writing tests that work in their environment.
 *
 */
public class JythonFactoryTest extends TestCase {
	
	@Test
	public void testFake() throws Exception {
		// Just a fake test, keeps mvn build happy
		// don't have real automated tests for build, current tests are just 
		// brittle tests for validating code by a user.  Testing needs 
		// DXL broker which is environment dependent
	}

//	@Test
//	public void testEvent() throws Exception {
//		String configFile = "temp/config/dxlclient.config";
//		String topic = "/my/service/test/topic";
//		String json = "{\"key\":\"value\"}";
//		
//
//		JythonFactory jf = JythonFactory.getInstance();
//		
//		DxlPublisherInterface dxl = jf.getDxlPublisherInterface();
//		if (dxl == null) {
//			fail("Was unable to get the Jython object");
//		}
//		
//		String result = dxl.sendMessage(configFile, topic, json);
//		System.out.println("Result: '" + result + "'");
//	}

//	@Test
//	public void testService() throws Exception {
//		String configFile = "temp/config/dxlclient.config";
//		String topic = "/my/service/test/topic";
//		String json = "{\"key\":\"value\"}";
//		
//
//		JythonFactory jf = JythonFactory.getInstance();
//		
//		DxlRequesterInterface dxl = jf.getDxlRequesterInterface();
//		if (dxl == null) {
//			fail("Was unable to get the Jython object");
//		}
//		
//		String result = dxl.sendMessage(configFile, topic, json);
//		System.out.println("Result: '" + result + "'");
//	}
}
