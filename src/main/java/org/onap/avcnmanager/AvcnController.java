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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableScheduling
public class AvcnController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvcnController.class);

    @Autowired
    private KafkaStreams kafkaStreams;

    @GetMapping("/healthcheck")
    public ResponseEntity healthcheck() {
        if(this.kafkaStreams.state().isRunning()) {
            return new ResponseEntity("Up", HttpStatus.EXPECTATION_FAILED);
        } else {
            return new ResponseEntity("Down", HttpStatus.EXPECTATION_FAILED);
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void scheduleFixedDelayTask() {
        LOGGER.info("### KAFKA scheduler:"+this.kafkaStreams.state());
        System.out.println("### KAFKA scheduler:"+this.kafkaStreams.state());
        if(!this.kafkaStreams.state().isRunning()){
            LOGGER.info("#### Restarting listening at Kafka...");
            System.out.println("#### Restarting listening at Kafka...");
            this.kafkaStreams.close();
            this.kafkaStreams.start();
        }
    }
}
