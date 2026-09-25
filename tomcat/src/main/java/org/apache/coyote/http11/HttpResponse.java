package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private HttpStatus status = HttpStatus.OK;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String body = "";

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setBody(ContentType contentType, String body) {
        headers.put("Content-Type", contentType.getValue());
        headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        this.body = body;
    }

    public void sendRedirect(String location) {
        this.status = HttpStatus.FOUND;
        headers.put("Location", location);
    }

    public byte[] toBytes() {
        List<String> lines = new ArrayList<>();
        lines.add("HTTP/1.1 " + status.getCode() + " " + status.getReasonPhrase() + " ");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            lines.add(header.getKey() + ": " + header.getValue() + " ");
        }
        lines.add("");
        lines.add(body);
        return String.join("\r\n", lines).getBytes(StandardCharsets.UTF_8);
    }
}
