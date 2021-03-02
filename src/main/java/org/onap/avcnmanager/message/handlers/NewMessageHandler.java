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

package org.onap.avcnmanager.message.handlers;

import java.util.Objects;
import org.onap.avcnmanager.message.forwarders.Forwarder;
import org.onap.avcnmanager.message.data.ChangePack;
import org.onap.avcnmanager.message.processing.TextProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class NewMessageHandler implements MessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(NewMessageHandler.class);

    private final TextProcessor processor;
    private final Forwarder forwarder;

    public NewMessageHandler(TextProcessor processor, Forwarder forwarder) {
        this.processor = processor;
        this.forwarder = forwarder;
    }

    @Override
    public void handleMessage(String key, ChangePack message) {
        LOGGER.info("Handling message: " + message.toString());
        stream(message)
                .filter(c -> !Objects.isNull(c))
                .filter(c -> !Objects.isNull(c.getNew()))
                .filter(c -> !Objects.isNull(c.getNew().getPath()))
                .filter(c -> !c.getNew().getPath().isEmpty())
                .map(c -> processor.process(key, message))
                .forEach(forwarder::send);
    }

    private Stream<ChangePack> stream(ChangePack changePack) {
        return Stream.of(changePack);
    }

}
