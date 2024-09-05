package org.apache.coyote.response;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ResponseHeaders implements Assemblable {

    private final Map<String, String> headers;

    protected ResponseHeaders() {
        this.headers = new HashMap<>();
    }

    @Override
    public void assemble(StringBuilder builder) {
        for (Entry<String, String> entry : headers.entrySet()) {
            builder.append(convert(entry));
        }
    }

    private String convert(Entry<String, String> entry) {
        return "%s: %s\r\n".formatted(entry.getKey(), entry.getValue());
    }
}
