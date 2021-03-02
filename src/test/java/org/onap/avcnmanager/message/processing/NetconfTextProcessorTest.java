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

package org.onap.avcnmanager.message.processing;

import org.junit.jupiter.api.Test;
import org.onap.avcnmanager.message.data.Change;
import org.onap.avcnmanager.message.data.ChangePack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NetconfTextProcessorTest {

    @Test
    void shouldReturnCorrentDnWithEmptyAttributeListForOneContainerOneListInstance() {
        ChangePack changePack = new ChangePack(new Change("/example-sports:sports/person[name='name 1']",""), emptyChange(), "1");

        String expectedDn = "example-sports:sports= example-sports:sports , person=name 1";
        ParsingResult expected = new ParsingResult(expectedDn, Collections.emptyMap());
        ParsingResult actual = new NetconfTextProcessor().process("aaa", changePack);

        assertEquals(expected.getDn(), actual.getDn());
        assertEquals(expected.getAttributesList(), actual.getAttributesList());
    }

    @Test
    void shouldReturnCorrentDnWithFilledAttributeListForOneContainerOneListOneValue() {
        ChangePack changePack = new ChangePack(new Change("/example-sports:sports/person[name='name 1']/name","name 2"), emptyChange(), "1");

        String expectedDn = "example-sports:sports= example-sports:sports , person=name 1";
        Map<String,String> expectedAttributesMap = new HashMap<>();
        expectedAttributesMap.put("name", "name 2");

        ParsingResult actual = new NetconfTextProcessor().process("aaa", changePack);

        assertEquals(expectedDn, actual.getDn());
        assertThat(actual.getAttributesList()).hasSameSizeAs(expectedAttributesMap);
        assertThat(actual.getAttributesList()).containsAllEntriesOf(expectedAttributesMap);
    }

    @Test
    void oneContainerTwoListsNestedNoValue() {
        ChangePack changePack = new ChangePack(
                new Change("/example-sports:sports/team[name='team 1']/player[name='player 1']",""), emptyChange(), "1");

        String expectedDn = "example-sports:sports= example-sports:sports , player=player 1";
        Map<String,String> expectedAttributesMap = Collections.emptyMap();

        ParsingResult actual = new NetconfTextProcessor().process("aaa", changePack);

        assertEquals(expectedDn, actual.getDn());
        assertThat(actual.getAttributesList()).hasSameSizeAs(expectedAttributesMap);
        assertThat(actual.getAttributesList()).containsAllEntriesOf(expectedAttributesMap);
    }

    @Test
    void oneContainerTwoListsNestedOneValue() {
        ChangePack changePack = new ChangePack(
                new Change("/example-sports:sports/team[name='team 1']/player[name='player 1']/name","player 1"), emptyChange(), "1");

        String expectedDn = "example-sports:sports= example-sports:sports , player=player 1";
        Map<String,String> expectedAttributesMap = new HashMap<>();
        expectedAttributesMap.put("name", "player 1");

        ParsingResult actual = new NetconfTextProcessor().process("aaa", changePack);

        assertEquals(expectedDn, actual.getDn());
        assertThat(actual.getAttributesList()).hasSameSizeAs(expectedAttributesMap);
        assertThat(actual.getAttributesList()).containsAllEntriesOf(expectedAttributesMap);
    }

    @Test
    void emptyChangeShouldGenerateEmptyResponse() {
        ChangePack changePack = new ChangePack(new Change("",""), emptyChange(), "1");

        String expectedDn = "";
        Map<String,String> expectedAttributesMap = new HashMap<>();

        ParsingResult actual = new NetconfTextProcessor().process("aaa", changePack);

        assertEquals(expectedDn, actual.getDn());
        assertThat(actual.getAttributesList()).hasSameSizeAs(expectedAttributesMap);
        assertThat(actual.getAttributesList()).containsAllEntriesOf(expectedAttributesMap);
    }

    private static Change emptyChange() {
        return new Change("", "");
    }
}
