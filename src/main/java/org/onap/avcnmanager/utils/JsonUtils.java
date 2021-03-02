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

package org.onap.avcnmanager.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onap.avcnmanager.message.processing.ParsingResult;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonUtils {
    private final ObjectMapper objectMapper;
    private final FileUtils ioUtils;

    public JsonUtils(FileUtils ioUtils) {
        this.objectMapper = new ObjectMapper();
        this.ioUtils = ioUtils;
    }

    public JsonNode convertToJsonNode(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String convertToJsonString(ParsingResult message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectNode objectNodeFromJsonNode(JsonNode jsonNode) {
        return (ObjectNode) jsonNode;
    }

    public JsonNode jsonNodeFromResourceFile(String fileName) {
        String json = ioUtils.readStringFromResourceFile(fileName);
        return convertToJsonNode(json);
    }

}
