package org.apache.coyote.http11.response;

import java.util.Map;

public class ResponseHeader {

    private static final String HEADER_DELIMITER = ": ";
    private static final String LINE_SEPARATOR = " \r\n";

    private final Map<String, String> values;

    public ResponseHeader(Map<String, String> values) {
        this.values = values;
    }

    public String toHeaderFieldMessage() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (String key : values.keySet()) {
            stringBuilder.append(key).append(HEADER_DELIMITER);
            stringBuilder.append(values.get(key)).append(LINE_SEPARATOR);
        }
        return stringBuilder.toString();
    }
}
