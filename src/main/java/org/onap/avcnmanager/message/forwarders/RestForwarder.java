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
import org.onap.avcnmanager.message.processing.ParsingResult;
import org.onap.avcnmanager.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestForwarder implements Forwarder {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestForwarder.class);
    private static final String PNF_PAYLOAD_TEMPLATE = "pnfsim_payload_notification.json";
    private final String pnfSimulatorEndpoint;
    private final RestTemplate restTemplate;
    private final String vesEndpoint;
    private final HttpHeaders headers;
    private final JsonNode pnfSimJson;
    private final JsonUtils jsonUtils;

    public RestForwarder(@Value("${rest.client.pnfsimulator.endpoint}") String pnfSimulatorEndpoint,
                         @Value("${rest.client.ves.endpoint}") String vesEndpoint,
                         RestTemplate restTemplate,
                         JsonUtils jsonUtils) {
        this.pnfSimulatorEndpoint = pnfSimulatorEndpoint;
        this.restTemplate = restTemplate;
        this.vesEndpoint = vesEndpoint;
        this.jsonUtils = jsonUtils;
        this.headers = new HttpHeaders();
        this.pnfSimJson = jsonUtils.jsonNodeFromResourceFile(PNF_PAYLOAD_TEMPLATE);
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Override
    public void send(ParsingResult message) {
        String resultAsJson = jsonUtils.convertToJsonString(message);
        JsonNode node = jsonUtils.convertToJsonNode(resultAsJson);
        String payload = fillSimulatorRequest(node);
        LOGGER.info("Will send payload: " + payload);
        sendPayload(payload);
    }


    private String fillSimulatorRequest(JsonNode node) {
        ObjectNode root = pnfSimJson.deepCopy();
        ((ObjectNode)root.get("simulatorParams")).put("vesServerUrl", vesEndpoint);
        root.set("variables", node);
        return root.toString();
    }

    private void sendPayload(String payload) {
        try {
            HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);
            restTemplate.postForObject(pnfSimulatorEndpoint, requestEntity, String.class);
        } catch (Throwable e) {
            LOGGER.error("Exception occurred: " + e.getMessage() + ", while sending: " + payload + ", the payload is skipped");
            LOGGER.debug("Exception details: ", e);
        }
    }
}
