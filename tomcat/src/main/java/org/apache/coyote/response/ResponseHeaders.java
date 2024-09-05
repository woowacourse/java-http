package org.apache.coyote.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ResponseHeaders implements Assemblable {

    private final Map<String, String> headers;

    public ResponseHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public void contentType(String contentType) {
        headers.put("Content-Type", "%s;charset=utf-8".formatted(contentType));
    }

    public void contentLength(int contentLength) {
        headers.put("Content-Length", String.valueOf(contentLength));
    }

    public void location(String location) {
        headers.put("Location", location);
    }

    @Override
    public void assemble(StringBuilder builder) {
        for (Entry<String, String> entry : headers.entrySet()) {
            builder.append(convert(entry));
        }
        builder.append("\r\n");
    }

    private String convert(Entry<String, String> entry) {
        return "%s: %s \r\n".formatted(entry.getKey(), entry.getValue());
    }
}
