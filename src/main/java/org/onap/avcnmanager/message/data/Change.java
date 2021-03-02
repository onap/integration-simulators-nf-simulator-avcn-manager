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

package org.onap.avcnmanager.message.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Change {
    private final String path;
    private final String value;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Change(@JsonProperty("path") String path,
                  @JsonProperty("value") String value) {
        this.path = path;
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "{" +
                "path='" + path + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Change change = (Change) o;
        return Objects.equals(getPath(), change.getPath()) &&
                Objects.equals(getValue(), change.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPath(), getValue());
    }
}
