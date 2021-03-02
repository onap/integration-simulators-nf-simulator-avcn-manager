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

import org.assertj.core.util.Maps;
import org.junit.jupiter.api.Test;
import org.onap.avcnmanager.message.data.Change;
import org.onap.avcnmanager.message.data.ChangePack;
import org.onap.avcnmanager.message.forwarders.Forwarder;
import org.onap.avcnmanager.message.processing.ParsingResult;
import org.onap.avcnmanager.message.processing.TextProcessor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class NewMessageHandlerTest {

    @Test
    void emptyChangeShouldBeSkippedFromProcessing() {
        TextProcessor mockProcessor = mock(TextProcessor.class);
        Forwarder mockForwarder = mock(Forwarder.class);
        NewMessageHandler handler = new NewMessageHandler(mockProcessor, mockForwarder);

        String key = "key";
        ChangePack message = new ChangePack(emptyChange(), emptyChange(), "");
        handler.handleMessage(key, message);

        verify(mockProcessor, never()).process(key, message);
        verify(mockProcessor, never()).process(anyString(), any(ChangePack.class));
        verify(mockForwarder, never()).send(any(ParsingResult.class));
    }

    @Test
    void validMessagesShouldBePassedToTextProcessor() {
        TextProcessor mockProcessor = mock(TextProcessor.class);
        Forwarder mockForwarder = mock(Forwarder.class);
        NewMessageHandler handler = new NewMessageHandler(mockProcessor, mockForwarder);

        String key = "key";
        ChangePack message = new ChangePack(new Change("aaaa", "bbbb"), emptyChange(), "1");
        handler.handleMessage(key, message);

        verify(mockProcessor, times(1)).process(key, message);
        verify(mockProcessor, times(1)).process(anyString(), any(ChangePack.class));
    }

    @Test
    void processedChangeShouldBePassedToForwarder() {
        List<ParsingResult> trapList = new ArrayList<>();
        String key = "key";
        ChangePack message = new ChangePack(new Change("aaaa", "bbbb"), emptyChange(), "1");

        TextProcessor mockProcessor = this::simpleProcessing;
        Forwarder mockForwarder = trapList::add;

        NewMessageHandler handler = new NewMessageHandler(mockProcessor, mockForwarder);
        handler.handleMessage(key, message);

        assertEquals(1, trapList.size());
        ParsingResult expected = simpleProcessing(key, message);
        assertEquals(expected, trapList.get(0));
    }

    private ParsingResult simpleProcessing(String key, ChangePack message) {
        return new ParsingResult(message.getNew().getPath(), Maps.newHashMap(message.getNew().getValue(), message.getType()));
    }

    private static Change emptyChange() {
        return new Change("", "");
    }
}
