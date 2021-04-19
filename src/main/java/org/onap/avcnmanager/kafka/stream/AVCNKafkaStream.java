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

package org.onap.avcnmanager.kafka.stream;

import org.apache.kafka.streams.KafkaStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@Component
public class AVCNKafkaStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(AVCNKafkaStream.class);

    private final KafkaStreams streams;

    @Autowired
    AVCNKafkaStream(KafkaStreams streams) {
        this.streams = streams;
    }

    @PostConstruct
    void startKafkaStream() {
        streams.setUncaughtExceptionHandler(this::handleExceptionInStreams);
        streams.start();
        LOGGER.info("The AVCN starts listening on Kafka messages ...");
    }

    @PreDestroy
    void stopKafkaStream() {
        streams.close();
    }

    private void handleExceptionInStreams(Thread thread,Throwable throwable) {
        LOGGER.error("Unexpected exception occurred in the Kafka stream. Prepare to shutdown ... ", throwable);
        streams.close();
    }
}


