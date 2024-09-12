package org.apache.catalina.parser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestParser {
    private static final String QUERY_PARAMETER_SEPARATOR = "&";
    private static final String QUERY_KEY_VALUE_DELIMITER = "=";
    private static final String HEADER_SEPARATOR = ": ";

    private RequestParser() {}

    public static Map<String, String> parseHeaders(List<String> headerLines) {
        return headerLines.stream()
                .skip(1)
                .map(line -> line.split(HEADER_SEPARATOR, 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> parts[0].trim(),
                        parts -> parts[1].trim()
                ));
    }

    public static Map<String, String> parseParamValues(String params) {
        return Arrays.stream(params.split(QUERY_PARAMETER_SEPARATOR))
                .map(param -> param.split(QUERY_KEY_VALUE_DELIMITER, 2))
                .filter(parts -> parts.length == 2 && parts[1] != null)
                .collect(Collectors.toMap(
                        parts -> parts[0].trim(),
                        parts -> parts[1].trim()
                ));
    }
}
