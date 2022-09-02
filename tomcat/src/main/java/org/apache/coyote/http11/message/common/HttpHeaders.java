package org.apache.coyote.http11.message.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class HttpHeaders {

    private static final String HEADER_DELIMITER_REGEX = ": ?";
    private static final String HEADER_DELIMITER = ": ";
    private static final String NEW_LINE = "\r\n";
    private static final String WHITE_SPACE = " ";

    private static final int SPLIT_LIMIT = 2;
    private static final int HEADER_NAME_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final Map<String, String> headers;

    public HttpHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = new LinkedHashMap<>(headers);
    }

    public HttpHeaders(final String headerText) {
        String[] splitHeaders = headerText.split(NEW_LINE);
        LinkedList<String> headerLines = new LinkedList<>(Arrays.asList(splitHeaders));

        HashMap<String, String> headers = new HashMap<>();
        for (String header : headerLines) {
            String[] splitHeader = header.split(HEADER_DELIMITER_REGEX, SPLIT_LIMIT);
            headers.put(splitHeader[HEADER_NAME_INDEX], splitHeader[HEADER_VALUE_INDEX]);
        }

        this.headers = headers;
    }

    public Optional<String> getHeader(final String header) {
        String value = headers.get(header);
        if (Objects.isNull(value)) {
            return Optional.empty();
        }

        return Optional.of(value);
    }

    public void addHeader(final String key, final String value) {
        headers.put(key, value);
    }

    public String generateHeaderText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String headerKey : headers.keySet()) {
            stringBuilder.append(headerKey)
                    .append(HEADER_DELIMITER)
                    .append(headers.get(headerKey))
                    .append(WHITE_SPACE)
                    .append(NEW_LINE);
        }

        return stringBuilder.toString();
    }
}
