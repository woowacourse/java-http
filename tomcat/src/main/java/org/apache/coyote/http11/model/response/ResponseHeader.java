package org.apache.coyote.http11.model.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public record ResponseHeader(
        Map<String, String> values
) {

    public ResponseHeader with(String name, String value) {
        Map<String, String> headers = new LinkedHashMap<>(values);
        headers.put(name, value);
        return new ResponseHeader(headers);
    }

    public String buildHeaderResponse() {
        StringBuilder builder = new StringBuilder();
        for (Entry<String, String> value : values.entrySet()) {
            builder.append(value.getKey()).
                    append(": ")
                    .append(value.getValue())
                    .append("\r\n");
        }
        builder.append("\r\n");
        return builder.toString();
    }
}
