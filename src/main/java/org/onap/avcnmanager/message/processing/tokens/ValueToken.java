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

package org.onap.avcnmanager.message.processing.tokens;

import org.onap.avcnmanager.message.processing.targets.TargetContainer;

import java.util.AbstractMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValueToken extends BaseToken {
    private static final Map.Entry<String,String> EMPTY_ENTRY = new AbstractMap.SimpleEntry<>("","");
    private static final Pattern PATTERN_VALUE = Pattern.compile("(.*)=(.*)");

    public ValueToken(String value) {
        super(value);
    }

    @Override
    public String stringValue() {
        return "";
    }

    @Override
    public Map.Entry<String, String> pairValue() {
        Map.Entry<String,String> entry = EMPTY_ENTRY;
        Matcher m = PATTERN_VALUE.matcher(getValue());
        if (m.find()) {
            String paramName = m.group(1).trim();
            String paramValue = m.group(2).trim();
            entry = new AbstractMap.SimpleEntry<>(paramName, paramValue);
        }
        return entry;
    }

    @Override
    public void dump(TargetContainer<String> targetContainer) {
        Map.Entry<String,String> entry = pairValue();
        targetContainer.acceptPair(entry.getKey(), entry.getValue());
    }
}
