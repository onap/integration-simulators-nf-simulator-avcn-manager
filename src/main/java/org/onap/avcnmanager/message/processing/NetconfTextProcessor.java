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

import org.onap.avcnmanager.message.data.ChangePack;
import org.onap.avcnmanager.message.processing.targets.MapTarget;
import org.onap.avcnmanager.message.processing.targets.StringBuilderTarget;
import org.onap.avcnmanager.message.processing.targets.TargetContainer;
import org.onap.avcnmanager.message.processing.tokens.ContainerToken;
import org.onap.avcnmanager.message.processing.tokens.ListToken;
import org.onap.avcnmanager.message.processing.tokens.Token;
import org.onap.avcnmanager.message.processing.tokens.ValueToken;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public final class NetconfTextProcessor implements TextProcessor {
    private static final Pattern PATTERN_PATH = Pattern.compile("/+");
    private static final String DELIMITER = " , ";

    @Override
    public ParsingResult process(String key, ChangePack message) {
        StringBuilderTarget dn = new StringBuilderTarget(new StringBuilder(), DELIMITER);
        MapTarget attributesMap = new MapTarget(new HashMap<>());
        LinkedList<Token> dequeOfTokens = new LinkedList<>();
        String newPath = message.getNew().getPath();
        String value = message.getNew().getValue();
        if(!value.isEmpty()) {
            newPath += " = " +value;
        }

        List<String> strings = splitToStrings(newPath, PATTERN_PATH);
        convertToTypedTokens(strings, dequeOfTokens, 0);
        removeConsecutiveDuplicatesFrom(dequeOfTokens);
        dumpTokensIntoRespectiveContainers(dequeOfTokens, dn, attributesMap);

        return emitResult(dn, attributesMap);
    }

    private static List<String> splitToStrings(String newPath, Pattern patternPath) {
        return patternPath.splitAsStream(newPath)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
    }

    private static void removeConsecutiveDuplicatesFrom(LinkedList<Token> dequeOfTokens) {
        List<Token> toRemove = new LinkedList<>();
        for (int i = 1; i < dequeOfTokens.size(); i++) {
            if(ListToken.class.equals(dequeOfTokens.get(i-1).getClass())
                && ListToken.class.equals(dequeOfTokens.get(i).getClass())) {
                toRemove.add(dequeOfTokens.get(i-1));
            }
        }
        dequeOfTokens.removeAll(toRemove);
    }

    private static void convertToTypedTokens(List<String> strings, LinkedList<Token> dequeOfTokens, int startIndex) {
        if (strings.size() > startIndex) {
            String str = strings.get(startIndex);
            Token token = determineToken(str);
            dequeOfTokens.add(token);

            convertToTypedTokens(strings,dequeOfTokens, startIndex + 1);
        }
    }

    private static Token determineToken(String str) {
        if (str.endsWith("]")) {
            return new ListToken(str);
        } else if (str.contains("=")) {
            return new ValueToken(str);
        } else {
            return new ContainerToken(str);
        }
    }

    private static void dumpTokensIntoRespectiveContainers(LinkedList<Token> dequeOfTokens,
                         TargetContainer<String> dnBuilder,
                         TargetContainer<String> attributesBuilder) {
        dequeOfTokens.forEach(t -> {
            t.dump(dnBuilder);
            t.dump(attributesBuilder);
        });
    }

    private static ParsingResult emitResult(StringBuilderTarget dnSb, MapTarget attributesMap) {
        dnSb.trimLastDelimiter();
        return new ParsingResult(dnSb.stringValue(), attributesMap.mapValue());
    }
}
