# BSD License
#
# Copyright 2018 AT&T Intellectual Property. All other rights reserved.
#
# Redistribution and use in source and binary forms, with or without modification, are permitted
# provided that the following conditions are met:
#
# 1. Redistributions of source code must retain the above copyright notice, this list of conditions
#    and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice, this list of
#    conditions and the following disclaimer in the documentation and/or other materials provided
#    with the distribution.
# 3. All advertising materials mentioning features or use of this software must display the
#    following acknowledgement:  This product includes software developed by the AT&T.
# 4. Neither the name of AT&T nor the names of its contributors may be used to endorse or
#    promote products derived from this software without specific prior written permission.
# 
# THIS SOFTWARE IS PROVIDED BY AT&T INTELLECTUAL PROPERTY ''AS IS'' AND ANY EXPRESS OR
# IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
# MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
# SHALL AT&T INTELLECTUAL PROPERTY BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
# SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
# PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS;
# OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
# ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
# DAMAGE.

from com.att.cso.opendxl.jython.client.interfaces import DxlPublisherInterface
from com.att.cso.opendxl.jython.client.exceptions import DxlJythonException

import logging
import os
import sys
import time

from dxlclient.client import DxlClient
from dxlclient.client_config import DxlClientConfig
from dxlclient.message import Event

# Enable logging, this will also direct built-in DXL log messages.
# See - https://docs.python.org/2/howto/logging-cookbook.html
log_formatter = logging.Formatter('%(asctime)s %(name)s - %(levelname)s - %(message)s')

console_handler = logging.StreamHandler()
console_handler.setFormatter(log_formatter)

logger = logging.getLogger()
if not len(logger.handlers):
    logger.addHandler(console_handler)
logger.setLevel(logging.WARN)


# Configure local logger
logger = logging.getLogger("EventPublisher")
logger.setLevel(logging.INFO)

class EventPublisher(DxlPublisherInterface):
    
    def __init__(self):
        self.client = None
        

    def connect(self, config_file="./dxlclient.config"):
        if self.isConnected():
            raise DxlJythonException(1100, "Already connected to the OpenDXL broker")
            
        try:
            logger.info("Reading configuration file from '%s'", config_file)
            config = DxlClientConfig.create_dxl_config_from_file(config_file)

            # Initialize DXL client using our configuration
            self.client = DxlClient(config)

            # Connect to DXL Broker
            self.client.connect()

            return
        except Exception as e:
            logger.info("Exception: " + e.message)
            raise DxlJythonException(1000, "Unable to establish a connection with the DXL broker")
        
        
    def sendMessage(self, topic="/dsa/dxl/test/event2", message="Default message"):
        if not self.isConnected():
            raise DxlJythonException(1200, "Not connected to a OpenDXL broker")
        
        try:
            event = Event(topic)

            # Encode string payload as UTF-8
            event.payload = message.encode()

            # Send event on DXL
            logger.info("Sending '" + message + "' to '" + topic + "'")
            self.client.send_event(event)

            return "Event successfully posted to topic '%s'" % topic

        except Exception as e:
            logger.info("Exception: " + e.message)
            raise DxlJythonException(1010, "Unable to communicate with a DXL broker")
        
        
    def disconnect(self):
        if not self.isConnected():
            return
        
        self.client.disconnect()
        
    def isConnected(self):
        if self.client is None:
            return False;
        return self.client.connected
    
    