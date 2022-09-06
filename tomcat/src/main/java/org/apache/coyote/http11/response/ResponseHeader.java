package org.apache.coyote.http11.response;

import java.util.Map;

public class ResponseHeader {

    private final Map<String, String> values;

    public ResponseHeader(Map<String, String> values) {
        this.values = values;
    }

    public String toHeaderFieldMessage() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (String key : values.keySet()) {
            stringBuilder.append(key + ": ");
            stringBuilder.append(values.get(key) + " \r\n");
        }
        return stringBuilder.toString();
    }
}
