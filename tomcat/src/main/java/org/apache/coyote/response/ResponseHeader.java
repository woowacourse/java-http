package org.apache.coyote.response;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.ContentType;

public class ResponseHeader {

    private final Map<String, String> values;

    private ResponseHeader(final Map<String, String> values) {
        this.values = values;
    }

    public static ResponseHeader of(final ContentType contentType, final String length) {
        final Map<String, String> values = new HashMap<>();
        values.put("Content-Type", contentType.getValue());
        values.put("Content-Length", length);
        return new ResponseHeader(values);
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();

        values.forEach((key, value) -> {
            result.append(key)
                    .append(": ")
                    .append(value)
                    .append(" ")
                    .append("\r\n");
        });

        return result.toString();
    }
}
