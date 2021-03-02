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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class ParsingResult {
    private final String dn;
    private final Map<String,String> attributesList;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ParsingResult(@JsonProperty("dn") String dn, @JsonProperty("attributesList") Map<String, String> attributesList) {
        this.dn = dn;
        this.attributesList = attributesList;
    }

    public String getDn() {
        return dn;
    }

    public Map<String, String> getAttributesList() {
        return Collections.unmodifiableMap(attributesList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParsingResult that = (ParsingResult) o;
        return Objects.equals(getDn(), that.getDn()) &&
                Objects.equals(getAttributesList(), that.getAttributesList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDn(), getAttributesList());
    }
}
