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

package org.onap.avcnmanager.message.serializers;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.onap.avcnmanager.message.data.Change;
import org.onap.avcnmanager.message.data.ChangePack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChangePackSerializersTest {
    private static Serializer<ChangePack> serializer;
    private static Deserializer<ChangePack> deserializer;
    private final List<ChangePack> testList;

    ChangePackSerializersTest() {
        testList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Change aNew = new Change(Integer.toString(i),Integer.toString(i));
            Change old = new Change(Integer.toString(i-1),Integer.toString(i-1));
            ChangePack changePack = new ChangePack(aNew, old, "1");
            testList.add(changePack);
        }
    }

    String[] getTopics() {
        return new String[] { "test" };
    }

    @BeforeAll
    static void setup() {
        serializer = new ChangePackSerializer();
        deserializer = new ChangePackDeserializer();
        serializer.configure(null, false);
        deserializer.configure(null, false);
    }

    @Test
    void shouldSerializeAndDeserializeCorrectly() {
        for(String topic : getTopics()) {
            testList.forEach(x -> {
                ChangePack changePack = deserializer.deserialize(topic, serializer.serialize(topic, x));
                assertThat(changePack).isNotSameAs(x);
                assertThat(changePack).isEqualTo(x);
            });
        }
    }

    @Test
    void shouldThrowWhenDeserializeGarbage() {
        byte[] bytes = serializer.serialize("", testList.get(0));
        new Random(System.currentTimeMillis()).nextBytes(bytes);

        assertThrows(RuntimeException.class, () -> deserializer.deserialize("", bytes));
    }

}
