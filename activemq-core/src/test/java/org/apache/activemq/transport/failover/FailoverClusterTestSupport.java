/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.transport.failover;

import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.network.NetworkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FailoverClusterTestSupport extends TestCase {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final int NUMBER_OF_CLIENTS = 30;

    private String clientUrl;

    private final Map<String, BrokerService> brokers = new HashMap<String, BrokerService>();
    private final List<ActiveMQConnection> connections = new ArrayList<ActiveMQConnection>();

    protected void assertClientsConnectedToTwoBrokers() {
        Set<String> set = new HashSet<String>();
        for (ActiveMQConnection c : connections) {
            set.add(c.getTransportChannel().getRemoteAddress());
        }
        assertTrue("Only 2 connections should be found: " + set,
                set.size() == 2);
    }

    protected void assertClientsConnectedToThreeBrokers() {
        Set<String> set = new HashSet<String>();
        for (ActiveMQConnection c : connections) {
            set.add(c.getTransportChannel().getRemoteAddress());
        }
        assertTrue("Only 3 connections should be found: " + set,
                set.size() == 3);
    }

    protected void addBroker(String name, BrokerService brokerService) {
        brokers.put(name, brokerService);
    }

    protected BrokerService getBroker(String name) {
        return brokers.get(name);
    }

    protected BrokerService removeBroker(String name) {
        return brokers.remove(name);
    }

    protected void destroyBrokerCluster() throws JMSException, Exception {
        for (BrokerService b : brokers.values()) {
            b.stop();
        }
        brokers.clear();
    }

    protected void shutdownClients() throws JMSException {
        for (Connection c : connections) {
            c.close();
        }
    }

    protected BrokerService createBroker(String brokerName) throws Exception {
        BrokerService answer = new BrokerService();
        answer.setPersistent(false);
        answer.setUseJmx(false);
        answer.setBrokerName(brokerName);
        answer.setUseShutdownHook(false);
        return answer;
    }

    protected void addTransportConnector(BrokerService brokerService,
                                         String connectorName, String uri, boolean clustered)
            throws Exception {
        TransportConnector connector = brokerService.addConnector(uri);
        connector.setName(connectorName);
        if (clustered) {
            connector.setRebalanceClusterClients(true);
            connector.setUpdateClusterClients(true);
            connector.setUpdateClusterClientsOnRemove(true);
        } else {
            connector.setRebalanceClusterClients(false);
            connector.setUpdateClusterClients(false);
            connector.setUpdateClusterClientsOnRemove(false);
        }
    }

    protected void addNetworkBridge(BrokerService answer, String bridgeName,
                                    String uri, boolean duplex, String destinationFilter)
            throws Exception {
        NetworkConnector network = answer.addNetworkConnector(uri);
        network.setName(bridgeName);
        network.setDuplex(duplex);
        if (destinationFilter != null && !destinationFilter.equals("")) {
            network.setDestinationFilter(bridgeName);
        }
    }

    @SuppressWarnings("unused")
    protected void createClients() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
                clientUrl);
        for (int i = 0; i < NUMBER_OF_CLIENTS; i++) {
            ActiveMQConnection c = (ActiveMQConnection) factory
                    .createConnection();
            c.start();
            Session s = c.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = s.createQueue(getClass().getName());
            MessageConsumer consumer = s.createConsumer(queue);
            connections.add(c);
        }
    }

    public String getClientUrl() {
        return clientUrl;
    }

    public void setClientUrl(String clientUrl) {
        this.clientUrl = clientUrl;
    }
}