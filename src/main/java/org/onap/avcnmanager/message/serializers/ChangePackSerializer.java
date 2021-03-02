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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.onap.avcnmanager.message.data.ChangePack;

import java.util.Map;

public class ChangePackSerializer implements Serializer<ChangePack> {
    private ObjectMapper objectMapper;
    @Override public void configure(Map<String, ?> configs, boolean isKey) {
        objectMapper = new ObjectMapper();
    }
    @Override
    public byte[] serialize(String s, ChangePack changePack) {
        byte[] retVal;
        try {
            retVal = objectMapper.writeValueAsBytes(changePack);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Can not serialize ChangePack instance: " + changePack + "\nReason: " + e);
        }
        return retVal;
    }
}
