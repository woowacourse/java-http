package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Headers {

    private static final String FIELD_VALUE_SEPARATOR = ": ";
    private static final String CRLF = "\r\n";

    private final Map<String, String> values;

    public Headers(final Map<String, String> values) {
        this.values = values;
    }

    public String format() {
        final List<String> headerLines = new ArrayList<>();
        for (Map.Entry<String,String> entry : values.entrySet()) {
            headerLines.add(String.join(FIELD_VALUE_SEPARATOR, entry.getKey(), entry.getValue()));
        }
        return String.join(CRLF, headerLines);
    }
}
