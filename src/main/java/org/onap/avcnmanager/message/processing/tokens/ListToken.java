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

public class ListToken extends BaseToken {
    private static final Map.Entry<String,String> EMPTY_ENTRY = new AbstractMap.SimpleEntry<>("","");
    private static final Pattern PATTERN_LIST = Pattern.compile("(.*)?\\[(.*)?='(.*)?'\\]");

    public ListToken(String value) {
        super(value);
    }

    @Override
    public String stringValue() {
        StringBuilder sb = new StringBuilder();
        Matcher m = PATTERN_LIST.matcher(getValue());
        if (m.find()) {
            String listName = m.group(1);
            String value = m.group(3);
            sb.append(listName).append("=").append(value);
        }
        return sb.toString();
    }

    @Override
    public Map.Entry<String, String> pairValue() {
        return EMPTY_ENTRY;
    }

    @Override
    public void dump(TargetContainer<String> targetContainer) {
        targetContainer.acceptOne(stringValue());
    }
}
