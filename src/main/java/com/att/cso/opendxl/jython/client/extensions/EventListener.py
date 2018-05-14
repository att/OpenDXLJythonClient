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

from com.att.cso.opendxl.jython.client.interfaces import DxlListenerInterface
from com.att.cso.opendxl.jython.client.interfaces import DxlCallbackInterface
from com.att.cso.opendxl.jython.client import DxlMessage as JavaDxlMessage
from com.att.cso.opendxl.jython.client.exceptions import DxlJythonException

import logging
import os
import sys
import time

from dxlclient.callbacks import EventCallback
from dxlclient.client import DxlClient
from dxlclient.client_config import DxlClientConfig

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
logger = logging.getLogger("EventListener")
logger.setLevel(logging.INFO)


class EventListener(DxlListenerInterface):

    def __init__(self):
        self.started = False
        self.loop = True
        
    def start(self, config_file="./dxlclient.config", topic="/dsa/dxl/test/event2", dxl_callback=None):
        if self.started:
            raise DxlJythonException(2000, "Already started")
        if not dxl_callback:
            raise DxlJythonException(2100, "DXL callback is required")
                
        try:
            logger.info("Starting event listener on topic '%s'", topic)
            logger.info("Reading configuration file from '%s'", config_file)
            config = DxlClientConfig.create_dxl_config_from_file(config_file)
              
            # Initialize DXL client using our configuration
            with DxlClient(config) as client:

                # Connect to DXL Broker
                client.connect()
            
                class MyEventCallback(EventCallback):
                    def __init__(self, dxl_callback):
                        self.dxlCallback = dxl_callback
                        
                    def on_event(self, event):
                        dxl_message = JavaDxlMessage()
                        dxl_message.setTopic(event.destination_topic)
                        dxl_message.setMessageVersion(event.version)
                        dxl_message.setMessageId(event.message_id)
                        dxl_message.setClientId(event.source_client_id)
                        dxl_message.setBrokerId(event.source_broker_id)
                        dxl_message.setMessageType(event.message_type)
                        dxl_message.setBrokerIdList(event.broker_ids)
                        dxl_message.setClientIdList(event.client_ids)
                        dxl_message.setPayload(event.payload.decode())

                        self.dxlCallback.callbackEvent(dxl_message)
                
                client.add_event_callback(str(topic), MyEventCallback(dxl_callback))
                
                self.started = True
                while self.loop:
                    time.sleep(1)
                
                logger.info("Shutting down event listener on topic '%s'", topic)
                return "Shutting down event listener on topic '%s'" % topic
                
        except Exception as e:
            logger.error("Exception $s", e.message)
            raise DxlJythonException(1010, "Unable to communicate with a DXL broker")

        
    def stop(self):
        self.started = False
        self.loop = False
