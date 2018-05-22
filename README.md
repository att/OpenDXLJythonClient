# Readme

## Jython OpenDXL Library

The Jython OpenDXL library is a small set of code that, when combined with 
Jython and OpenDXL client code, provides easy to use Java based access to 
the OpenDXL fabric.  Simple connections are provided to allow Java applications
to publish events, request services, listen for events or provide a service.

&nbsp;
## Requirements

 * jython-standalone 2.7.1
 * OpenDXL Client library 3.0.1 (included in resources dir)

&nbsp;
## Installation

Note: The Jython offical page lists 2.7.0 as the latest release however,
2.7.1 for the jython-standalone has been released to Maven and has bug fixes
that are critical for establishing the secure connection to the DXL fabric.

Note: The OpenDXL client library has been stored in the src/main/resources
folder and the msgpack and paho folders have been pulled out of dxlclient/_vendor
to ensure all three packages are at the root level of the jar file so 
Jython can find them.

Download the source and run a maven build to create the library.

```
mvn clean install
```

&nbsp;
### Maven use in your project

Add this dependency to your project's POM:

```xml
<dependency>
	<groupId>com.att.cso</groupId>
	<artifactId>opendxl.jython.client</artifactId>
	<version>0.0.4</version>		
</dependency>
```
&nbsp;
### Maven use in a SpringFramework project

Spring framework adds a twist to using the Jython library.  Due to the way that
Spring packages external jar files, the Jython library is not able to find the 
Lib directory contained in it's jar.  In order to get the library to work with
SpringFramework, you need to unpack the Jython jar.

To unpack, add the configuration section below to the plugins in your build section.
This step is not necessary for the examples below.

```xml
<plugins>
	<plugin>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-maven-plugin</artifactId>
		<configuration>
			<requiresUnpack>
				<dependency>
					<groupId>org.python</groupId>
					<artifactId>jython-standalone</artifactId>
					<version>2.7.1</version>
				</dependency>
			</requiresUnpack>
		</configuration>
	</plugin>
</plugins>
```
&nbsp;
## Documentation

Please see the [Java docs](https://github.com/att/OpenDXLJythonClient/tree/master/doc) for the most up-to-date documentation.

&nbsp;
## Examples

The following are example programs to test the library.

The pom file is the same with different artifactId, name and mainclass so it will not be repeated 
for each example.

#### pom.xml

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.att.cso</groupId>
  <artifactId>OpendxlEventPublisher</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <packaging>jar</packaging>

  <name>OpendxlEventPublisher</name>
  <url>http://maven.apache.org</url>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
	<java.version>1.7</java.version>
  </properties>

  <dependencies>
  	<dependency>
		<groupId>com.att.cso</groupId>
		<artifactId>opendxl.jython.client</artifactId>
		<version>0.0.4</version>
	</dependency>
  </dependencies>
  
  <build>
    <sourceDirectory>src/main/java</sourceDirectory>
    <plugins>
      <plugin>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.3</version>
        <configuration>
          <source>${java.version}</source>
          <target>${java.version}</target>
        </configuration>
      </plugin>
		<!-- Maven Assembly Plugin -->
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-assembly-plugin</artifactId>
			<version>2.4.1</version>
			<configuration>
				<archive>
					<manifest>
						<mainClass>com.att.cso.opendxl.EventPublisherMain</mainClass>
					</manifest>
				</archive>
				<!-- ## get all project dependencies -->
				<descriptorRefs>
					<descriptorRef>jar-with-dependencies</descriptorRef>
				</descriptorRefs>
			</configuration>
			<executions>
				<execution>
					<id>make-assembly</id>
					<!-- bind to the packaging phase -->
					<phase>package</phase>
					<goals>
						<goal>single</goal>
					</goals>
				</execution>
			</executions>
		</plugin>
    </plugins>
  </build>
</project>
```
&nbsp;
### Event Publisher

#### EventPublisherMain.java

```java
package com.att.cso.opendxl;

import com.att.cso.opendxl.jython.client.JythonFactory;
import com.att.cso.opendxl.jython.client.exceptions.DxlJythonException;
import com.att.cso.opendxl.jython.client.interfaces.DxlPublisherInterface;

public class EventPublisherMain {
	public static boolean MSG_RECEIVED = false;
	
	public static void main(String[] args) {
		String configFile = "/home/myhome/temp/pyconfig/dxlclient.config";
		String topic = "/my/event/test/dxlJythonTest";
		String message = "This is a test event from OpendxlEventPublisher";

		JythonFactory jf = JythonFactory.getInstance();

		DxlPublisherInterface dxl = jf.getDxlPublisherInterface();
		if (dxl == null) {
			System.out.println("Was unable to get the Jython object");
			System.exit(0);
		}

		String result;
		
		try {
			dxl.connect(configFile);
			if (!dxl.isConnected()) {
				System.out.println("Something bad happened and we weren't able to connect");
				System.exit(0);
			}
		} catch (DxlJythonException e) {
			System.out.println("Caught a DxlJythonException trying to connect, exiting program");
			System.exit(0);
		}
		
	
		try {

			result = dxl.sendMessage(topic, message);
			System.out.println("Result: '" + result + "' for message '" + message + "'");

			try {
				Thread.sleep(10000); // 10 seconds
			} catch (InterruptedException e) { } 
		
			result = dxl.sendMessage(topic, "Stop listener");
			System.out.println("Stop message result: '" + result + "'");
		} catch (DxlJythonException e) {
			System.out.println("Exception caught trying to send messages on the DXL broker");
		}
		
		dxl.disconnect();
		if (dxl.isConnected()) {
			System.out.println("Something bad happened and we may still be connected to the broker");
		}
	}

}
```
&nbsp;
### Event Listener

#### DxlCallbackImplementer.java

```java
package com.att.cso.opendxl;

import com.att.cso.opendxl.jython.client.DxlMessage;
import com.att.cso.opendxl.jython.client.interfaces.DxlCallbackInterface;

public class DxlCallbackImplementer implements DxlCallbackInterface {

	@Override
	public String callbackEvent(DxlMessage message) {
		message.printDxlMessage();
		if (message.getPayload().contains("Stop listener"))
			EventListenerMain.SHUTDOWN_RECEIVED = true;
		return "Successful";
	}

}
```
&nbsp;
#### EventListenerMain.java

```java
package com.att.cso.opendxl;

import java.io.File;

import com.att.cso.opendxl.jython.client.JythonFactory;
import com.att.cso.opendxl.jython.client.exceptions.DxlJythonException;
import com.att.cso.opendxl.jython.client.interfaces.DxlListenerInterface;

public class EventListenerMain {
	public static boolean SHUTDOWN_RECEIVED = false;
	
	public static void main(String[] args) {
		
		System.out.println("Path: " + (new File(".")).getAbsolutePath());
		
		EventThread thread = new EventThread();
		thread.start();
			
		while (!SHUTDOWN_RECEIVED) {
			System.out.println("Waiting for message for service...");
			try {
				Thread.sleep(10000); // 10 seconds
			} catch (InterruptedException e) { }
		}
		System.out.println("Exited event listener loop...");

		thread.getInterface().stop();
		System.out.println("Issued stop to Python loop");
	}

}

class EventThread extends Thread {
	String configFile = "/home/myhome/temp/pyconfig/dxlclient.config";
	String topic = "/my/event/test/dxlJythonTest";
	private DxlListenerInterface dxl = null;
	
	public void run() {
		JythonFactory jf = JythonFactory.getInstance();
		
		dxl = jf.getDxlListenerInterface();
		if (dxl == null) {
			System.out.println("Unable to get the Jython provider object");
			System.exit(0);
		}
		
		DxlCallbackImplementer dxlCallback = new DxlCallbackImplementer();
		try {
			dxl.start(configFile, topic, dxlCallback);
		} catch (DxlJythonException e) {
			System.out.println("Exception caught starting the provider");
			System.out.println("ErrorCode: " + e.getErrorCode() + "  Error message: '" + e.getMessage() + "'");
			dxl.stop();
			System.exit(0);
		}
	}
	
	public DxlListenerInterface getInterface() {
		return dxl;
	}

}
```
&nbsp;
### Service Requester

#### ServiceRequesterMain.java

```java
package com.att.cso.opendxl;

import java.io.File;

import com.att.cso.opendxl.jython.client.DxlMessage;
import com.att.cso.opendxl.jython.client.JythonFactory;
import com.att.cso.opendxl.jython.client.exceptions.DxlJythonException;
import com.att.cso.opendxl.jython.client.interfaces.DxlRequesterInterface;

public class DXLMain {
	public static boolean MSG_RECEIVED = false;
	
	public static void main(String[] args) {
		String configFile = "/home/myhome/temp/pyconfig/dxlclient.config";
		String topic = "/my/service/test/dxlJythonTest";
		String message = "This is a test event from OpendxlServiceRequester";
		

		JythonFactory jf = JythonFactory.getInstance();
		
		DxlRequesterInterface dxl = jf.getDxlRequesterInterface();
		if (dxl == null) {
			System.out.println("Was unable to get the Jython object");
			System.exit(0);
		}
		
		DxlMessage result;
		
		try {
			dxl.connect(configFile);
			if (!dxl.isConnected()) {
				System.out.println("Something bad happened and we weren't able to connect");
				System.exit(0);
			}
		} catch (DxlJythonException e) {
			System.out.println("Caught a DxlJythonException trying to connect, exiting program");
			System.exit(0);
		}
		
		try {
			int idx=0;
			for (idx=0; idx<10; idx++) {
				result = dxl.sendMessage(topic, message + " " + idx);
				System.out.println("Results:");
				result.printDxlMessage();
			}
		
			try {
				Thread.sleep(10000); // 10 seconds
			} catch (InterruptedException e) { } 
		
			result = dxl.sendMessage(topic, "Stop listener");
			System.out.println("Stop message result:");
			result.printDxlMessage();
		} catch (DxlJythonException e) {
			System.out.println("Exception caught trying to send messages on the DXL broker");
		}
		
		dxl.disconnect();
		if (dxl.isConnected()) {
			System.out.println("Something bad happened and we may still be connected to the broker");
		}
	} 

}
```
&nbsp;
### Service Provider

#### DxlCallbackImplementer.java

```java
package com.att.cso.opendxl;

import com.att.cso.opendxl.jython.client.DxlMessage;
import com.att.cso.opendxl.jython.client.interfaces.DxlCallbackInterface;

public class DxlCallbackImplementer implements DxlCallbackInterface {

	@Override
	public String callbackEvent(DxlMessage message) {
		message.printDxlMessage();
		if (message.getPayload().contains("Stop listener"))
			DXLMain.SHUTDOWN_RECEIVED = true;
		return "Successful";
	}

}
```
&nbsp;
#### ServiceProviderMain.java

```java
package com.att.cso.opendxl;

import com.att.cso.opendxl.jython.client.JythonFactory;
import com.att.cso.opendxl.jython.client.exceptions.DxlJythonException;
import com.att.cso.opendxl.jython.client.interfaces.DxlProviderInterface;

public class DXLMain {
	public static boolean SHUTDOWN_RECEIVED = false;
	
	public static void main(String[] args) {

		ServiceThread thread = new ServiceThread();
		thread.start();

		while (!SHUTDOWN_RECEIVED) {
			System.out.println("Waiting for message for service...");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) { }
		}
		System.out.println("Exited service loop...");

		thread.getInterface().stop();
		System.out.println("Issued stop to Python loop");
	}

}

class ServiceThread extends Thread {
	private String configFile = "/home/myhome/temp/pyconfig/dxlclient.config";
	private String topic = "/my/service/test/dxlJythonTest";
	private DxlProviderInterface dxl = null;
	
	public void run() {
		JythonFactory jf = JythonFactory.getInstance();
		
		dxl = jf.getDxlProviderInterface();
		if (dxl == null) {
			System.out.println("Unable to get the Jython provider object");
			System.exit(0);
		}
		
		DxlCallbackImplementer dxlCallback = new DxlCallbackImplementer();
		try {
			dxl.start(configFile, "/att/test/service", topic, dxlCallback);
		} catch (DxlJythonException e) {
			System.out.println("Exception caught starting the provider");
			System.out.println("ErrorCode: " + e.getErrorCode() + "  Error message: '" + e.getMessage() + "'");
			dxl.stop();
			System.exit(0);
		}
	}
	
	public DxlProviderInterface getInterface() {
		return dxl;
	}

}
```

