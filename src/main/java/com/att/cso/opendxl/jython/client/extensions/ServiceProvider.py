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

from com.att.cso.opendxl.jython.client.interfaces import DxlProviderInterface
from com.att.cso.opendxl.jython.client.interfaces import DxlCallbackInterface
from com.att.cso.opendxl.jython.client import DxlMessage as JavaDxlMessage
from com.att.cso.opendxl.jython.client.exceptions import DxlJythonException

import logging
import os
import sys
import time

from dxlclient.callbacks import RequestCallback
from dxlclient.client import DxlClient
from dxlclient.client_config import DxlClientConfig
from dxlclient.message import Response
from dxlclient.service import ServiceRegistrationInfo

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
logger = logging.getLogger("ServiceProvider")
logger.setLevel(logging.INFO)

class ServiceProvider(DxlProviderInterface):

    def __init__(self):
        self.started = False
        self.loop = True

    def start(self, config_file="./dxlclient.config", service="/dsa/dxl/test", *args):
        if self.started:
            raise DxlJythonException(2000, "Already started")

        if len(args) == 2:
            # Topic and callback were specified in separate parameters
            if not args[1]:
                raise DxlJythonException(2100, "DXL callback is required")
            topic = args[0] or "/dsa/dxl/test/event2"
            callbacks_by_topic = {topic: args[1]}
            topic_info = "topic '%s'" % topic
        elif len(args) == 1:
            # Topics/callbacks were specified in a map
            callbacks_by_topic = args[0]
            topic_info = "topics '%s'" % ",".join(list(callbacks_by_topic.keys()))
        else:
            raise DxlJythonException(2100, "DXL callback is required")

        try:
            logger.info("Starting service '%s' on %s", service, topic_info)
            logger.info("Reading configuration file from '%s'", config_file)
            config = DxlClientConfig.create_dxl_config_from_file(config_file)
              
            # Initialize DXL client using our configuration
            with DxlClient(config) as client:
                # Connect to DXL Broker
                client.connect()

                class MyRequestCallback(RequestCallback):
                    def __init__(self, dxl_callback):
                        self.dxlCallback = dxl_callback

                    def on_request(self, request):
                        dxl_message = JavaDxlMessage()
                        dxl_message.setTopic(request.destination_topic)
                        dxl_message.setMessageVersion(request.version)
                        dxl_message.setMessageId(request.message_id)
                        dxl_message.setClientId(request.source_client_id)
                        dxl_message.setBrokerId(request.source_broker_id)
                        dxl_message.setMessageType(request.message_type)
                        dxl_message.setBrokerIdList(request.broker_ids)
                        dxl_message.setClientIdList(request.client_ids)
                        dxl_message.setReplyTopic(request.reply_to_topic)
                        dxl_message.setServiceId(request.service_id)
                        dxl_message.setPayload(request.payload.decode())
                        dxl_message.setReplyTopic(request.reply_to_topic)
                        dxl_message.setServiceId(request.service_id)

                        response = Response(request)
                        resp = self.dxlCallback.callbackEvent(dxl_message)
                        response.payload = resp.encode()
                        client.send_response(response)

                # Create DXL Service Registration object
                service_registration_info = ServiceRegistrationInfo(client, str(service))

                # Add topics for the service to respond to
                service_registration_info.add_topics(
                    {str(k): MyRequestCallback(v)
                     for k, v in callbacks_by_topic.iteritems()})

                # Register the service with the DXL fabric (with a wait up to 10 seconds for registration to complete)
                client.register_service_sync(service_registration_info, 10)
                
                self.started = True
                while self.loop:
                    time.sleep(1)

                logger.info("Shutting down service '%s' on %s", service, topic_info)
                return "Shutting down service provider on %s" % topic_info
                
        except Exception as e:
            logger.error("Exception $s", e.message)
            raise DxlJythonException(1010, "Unable to communicate with a DXL broker")
        
    def stop(self):
        logger.info("Stopping service")
        self.started = False
        self.loop = False
