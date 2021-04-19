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
package org.onap.avcnmanager;


import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@EnableScheduling
public class AvcnManagerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvcnManagerController.class);

    private final Topology topology;
    private final Properties properties;

    private final ReentrantLock lock = new ReentrantLock();
    private KafkaStreams kafkaStreams;

    public AvcnManagerController(@Qualifier("AVCNTopology") Topology topology,
                                 @Qualifier("AVCNProperties") Properties properties) {
        this.topology = topology;
        this.properties = properties;
    }

    @GetMapping("/healthcheck")
    public ResponseEntity healthcheck() {
        if (kafkaStreams != null && kafkaStreams.state().isRunning()) {
            return new ResponseEntity("Up", HttpStatus.OK);
        } else {
            return new ResponseEntity("Down", HttpStatus.EXPECTATION_FAILED);
        }
    }

    @Scheduled(fixedDelay = 10000)
    public void configureKafkaStream() {
        try {
            lock.lock();

            if (kafkaStreams == null) {
                startKafkaStream();
            } else if (!kafkaStreams.state().isRunning()) {
                LOGGER.info("KafkaStream is not running. Trying to restart ...");
                kafkaStreams.close(Duration.ZERO);
                startKafkaStream();
            }
        } catch (Exception e) {
            LOGGER.info("Unable to configure and start KafkaStream.",e);
            this.kafkaStreams = null;
        } finally {
            lock.unlock();
        }
    }

    private void startKafkaStream() {
        LOGGER.info("Starting KafkaStream ...");
        kafkaStreams = new KafkaStreams(topology, properties);
        kafkaStreams.setUncaughtExceptionHandler(
                (thread, throwable) -> System.out.printf(
                                "Error occurs during data KafkaStream processing. Thread: %s, Throwable: %s%n",
                                thread, throwable)
        );
        kafkaStreams.start();
    }

    @PreDestroy
    public void preDestroy() {
        try {
            lock.lock();
            if (kafkaStreams != null) {
                kafkaStreams.close(Duration.ZERO);
            }
        } finally {
            lock.unlock();
        }
    }
}
