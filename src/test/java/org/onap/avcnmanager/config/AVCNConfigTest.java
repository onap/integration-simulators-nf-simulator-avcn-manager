/*-
 * ============LICENSE_START=======================================================
 * Simulator
 * ================================================================================
 * Copyright (C) 2021 Nokia. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.avcnmanager.config;

import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.avcnmanager.message.handlers.MessageHandler;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AVCNConfigTest {


    private AVCNConfig kafkaStreamConfig;

    @Mock
    private MessageHandler messageHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        kafkaStreamConfig = new AVCNConfig();
    }

    @Test
    void validateKafkaStreamPropertiesAreCorrect() {
        final String testServer = "http://kafka/test";
        final String testApplication = "applicationTest";

        Properties properties = kafkaStreamConfig.getKafkaStreamProperties(testServer, testApplication);

        assertEquals(properties.getProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG), testServer);
        assertEquals(properties.getProperty(StreamsConfig.APPLICATION_ID_CONFIG), testApplication);
    }

    @Test
    void validateKafkaStreamTopologyIsCorrect() {
        final String testTopic = "testTopic";

        Topology topology = kafkaStreamConfig.getKafkaStreamTopology(testTopic, messageHandler);

        assertTrue(topology.describe().toString().contains(testTopic));
    }

}
