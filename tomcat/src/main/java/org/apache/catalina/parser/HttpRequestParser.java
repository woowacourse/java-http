package org.apache.catalina.parser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestParser {

    private static final String QUERY_KEY_VALUE_DELIMITER = "=";
    private static final String HEADER_SEPARATOR = ": ";
    private static final int REQUEST_LINE_INDEX = 1;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int PART_COUNT = 2;

    private HttpRequestParser() {}

    public static Map<String, String> parseHeaders(List<String> headerLines) {
        List<String> params = headerLines.stream()
                .skip(REQUEST_LINE_INDEX)
                .toList();
        return parseParamValues(params, HEADER_SEPARATOR);

    }

    public static Map<String, String> parseParamValues(List<String> params) {
        return parseParamValues(params, QUERY_KEY_VALUE_DELIMITER);
    }

    private static Map<String, String> parseParamValues(List<String> params, String delimiter) {
        return params.stream()
                .map(param -> List.of(param.split(delimiter, PART_COUNT)))
                .filter(parts -> parts.size() == PART_COUNT)
                .collect(Collectors.toMap(
                        parts -> parts.get(KEY_INDEX).trim(),
                        parts -> parts.get(VALUE_INDEX).trim()
                ));
    }
}
