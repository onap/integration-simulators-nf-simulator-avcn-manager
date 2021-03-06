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

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.streams.kstream.KStream;
import org.onap.avcnmanager.message.data.ChangePack;
import org.onap.avcnmanager.message.handlers.MessageHandler;
import org.onap.avcnmanager.message.serializers.ChangePackDeserializer;
import org.onap.avcnmanager.message.serializers.ChangePackSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

@Configuration
public class AVCNConfig {

    private static final int RETRIES_CONFIG = 5;
    private static final int RETRY_BACKOFF_MS = 60000;

    @Bean(name = "AVCNProperties")
    public Properties getKafkaStreamProperties(
            @Value("${kafka.bootstrap-servers}") String bootstrapServer,
            @Value("${kafka.application.id}") String applicationId
    ) {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, ChangePackSerde.class.getName());
        properties.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndContinueExceptionHandler.class);
        properties.put(StreamsConfig.RETRIES_CONFIG, RETRIES_CONFIG);
        properties.put(StreamsConfig.RETRY_BACKOFF_MS_CONFIG, RETRY_BACKOFF_MS);

        return properties;
    }

    @Bean(name = "AVCNTopology")
    public Topology getKafkaStreamTopology(
            @Value("${kafka.source.topic}") String inTopic,
            MessageHandler messageHandler
    ) {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, ChangePack> stream = builder.stream(inTopic);
        stream.foreach(messageHandler::handleMessage);
        return builder.build();
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    public static final class ChangePackSerde extends Serdes.WrapperSerde<ChangePack> {
        public ChangePackSerde() {
            super(new ChangePackSerializer(), new ChangePackDeserializer());
        }
    }
}
