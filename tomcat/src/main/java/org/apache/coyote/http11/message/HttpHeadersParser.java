package org.apache.coyote.http11.message;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class HttpHeadersParser {

    private static final String HEADER_LINE_DELIMITER = ":\\s?";
    private static final String HEADER_FIELDS_DELIMITER = ";\\s?";
    private static final int HEADER_NAME_INDEX = 0;
    private static final int HEADER_FIELDS_INDEX = 1;

    private HttpHeadersParser() {
    }

    public static HttpHeaders parseToHeaders(List<String> headerLines) {
        return new HttpHeaders(headerLines.stream()
                .map(HttpHeadersParser::parseHeaderLine)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
    }

    private static Map.Entry<String, List<String>> parseHeaderLine(String headerLine) {
        String[] headerLineElements = headerLine.split(HEADER_LINE_DELIMITER);
        String headerName = headerLineElements[HEADER_NAME_INDEX];
        String headerFields = headerLineElements[HEADER_FIELDS_INDEX];

        return Map.entry(headerName, parseHeaderFields(headerFields));
    }

    private static List<String> parseHeaderFields(String headerFields) {
        return Arrays.asList(headerFields.split(HEADER_FIELDS_DELIMITER));
    }
}
