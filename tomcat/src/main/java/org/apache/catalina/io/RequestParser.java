package org.apache.catalina.io;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestParser {
    private static final Logger log = LoggerFactory.getLogger(RequestParser.class);
    private static final String QUERY_PARAMETER_SEPARATOR = "&";
    private static final String QUERY_KEY_VALUE_DELIMITER = "=";
    public static final String HEADER_SEPARATOR = ": ";

    private RequestParser() {}

    public static Map<String, String> parseHeaders(List<String> headerLines) {
        return headerLines.stream()
                .skip(1)
                .map(line -> line.split(HEADER_SEPARATOR, 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> parts[0],
                        parts -> parts[1]
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
