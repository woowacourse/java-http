package org.apache.coyote.http11.header;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.coyote.http11.converter.HeaderStringConverter;

public class Headers {

    private static final String HEADER_DELIMITER = ": ";
    private static final int HEADER_PARTS_COUNT = 2;
    private static final int HEADER_NAME_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final Map<String, String> values;

    public Headers(final List<String> header) {
        this.values = parseHeader(header);
    }

    public static Headers empty() {
        return new Headers(Collections.emptyList());
    }

    private Map<String, String> parseHeader(final List<String> headerLines) {
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        
        headerLines.stream()
            .map(line -> line.split(HEADER_DELIMITER, HEADER_PARTS_COUNT))
            .filter(headerParts -> headerParts.length == HEADER_PARTS_COUNT)
            .forEach(headerParts -> headers.put(
                headerParts[HEADER_NAME_INDEX],
                headerParts[HEADER_VALUE_INDEX]
            ));

        return headers;
    }

    public boolean containsHeader(final String headerName) {
        return values.containsKey(headerName);
    }

    public String getHeaderValue(final String headerName) {
        return values.get(headerName);
    }

    public Map<String, String> getAllHeaders() {
        return new HashMap<>(values);
    }

    public boolean containsAll(final Headers other) {
        return Objects.equals(values, other.values);
    }

    public String toMessageFormat() {
        final StringBuilder messageBuilder = new StringBuilder();

        final String headersMessage = values.entrySet().stream()
            .map(entry -> HeaderStringConverter.convert(entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(System.lineSeparator()));
        messageBuilder.append(headersMessage);
        messageBuilder.append(System.lineSeparator());

        return messageBuilder.toString();
    }

    @Override
    public String toString() {
        return values.entrySet().stream()
            .map(entry -> entry.getKey() + " = " + entry.getValue())
            .collect(Collectors.joining(System.lineSeparator()));
    }
}
