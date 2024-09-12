package org.apache.coyote.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QueryParameterParser {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int PARAMETER_KEY_INDEX = 0;
    private static final int PARAMETER_VALUE_INDEX = 1;

    private QueryParameterParser() {
    }

    public static Map<String, List<String>> parse(String queryParameter) {
        if (queryParameter == null || queryParameter.isBlank()) {
            return Map.of();
        }
        return Arrays.stream(queryParameter.split(PARAMETER_DELIMITER))
                .map(queryString -> queryString.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(QueryParameterParser::parseKey, QueryParameterParser::parseValue, merge()));
    }

    private static String parseKey(String[] queryParameter) {
        return queryParameter[PARAMETER_KEY_INDEX];
    }

    private static List<String> parseValue(String[] queryParameter) {
        if (queryParameter.length == 1) {
            return new ArrayList<>(List.of(""));
        }
        return new ArrayList<>(List.of(queryParameter[PARAMETER_VALUE_INDEX]));
    }

    private static BinaryOperator<List<String>> merge() {
        return (existingList, newList) -> {
            existingList.addAll(newList);
            return existingList;
        };
    }
}
