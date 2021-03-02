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

package org.onap.avcnmanager.message.processing.targets;

public class StringBuilderTarget implements TargetContainer<String> {
    private final StringBuilder stringBuilder;
    private final String delimiter;

    public StringBuilderTarget(StringBuilder stringBuilder, String delimiter) {
        this.stringBuilder = stringBuilder;
        this.delimiter = delimiter;
    }

    @Override
    public void acceptOne(String argument) {
        stringBuilder.append(argument).append(delimiter);
    }

    @Override
    public void acceptPair(String first, String second) {
        //no op
    }

    public String stringValue() {
        return stringBuilder.toString();
    }

    public void trimLastDelimiter() {
        if(this.stringBuilder.lastIndexOf(delimiter) == this.stringBuilder.length() - delimiter.length()) {
            this.stringBuilder.setLength(this.stringBuilder.length() - delimiter.length());
        }
    }
}
