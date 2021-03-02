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

package org.onap.avcnmanager.message.forwarders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.onap.avcnmanager.message.processing.ParsingResult;
import org.onap.avcnmanager.utils.FileUtils;
import org.onap.avcnmanager.utils.JsonUtils;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RestForwarderTest {
    private final JsonUtils jsonUtils;

    RestForwarderTest() {
        jsonUtils = new JsonUtils(new FileUtils());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldSendResultsAsValidPnfSimRequest() {
        RestTemplate restTemplateMock = mock(RestTemplate.class);

        String pnfSimulatorEndpoint = "http://pnfsim_endpoint";
        String vesEndpoint = "http://some_ves_endpoint";
        String expectedPayload = new FileUtils().readStringFromResourceFile("sample_payload_notification.json");
        JsonNode expectedJson = jsonUtils.convertToJsonNode(expectedPayload);
        ((ObjectNode)expectedJson.get("simulatorParams")).put("vesServerUrl", vesEndpoint);
        Map<String,String> sampleAttributes = new HashMap<>();
        sampleAttributes.put("attribute1", "value1");
        ParsingResult parsingResult = new ParsingResult("some/dn", sampleAttributes);

        ArgumentCaptor<HttpEntity<String>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        ArgumentCaptor<String> endpointCaptor = ArgumentCaptor.forClass(String.class);

        RestForwarder restForwarder = new RestForwarder(pnfSimulatorEndpoint, vesEndpoint, restTemplateMock, jsonUtils);
        restForwarder.send(parsingResult);

        verify(restTemplateMock).postForObject(endpointCaptor.capture(), entityCaptor.capture(), eq(String.class));
        String stringBody = entityCaptor.getValue().getBody();
        JsonNode node = jsonUtils.convertToJsonNode(stringBody);
        assertEquals(expectedJson, node);
        assertEquals(pnfSimulatorEndpoint, endpointCaptor.getValue());
    }
}
