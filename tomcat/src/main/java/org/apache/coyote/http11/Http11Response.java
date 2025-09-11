package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Http11Response {

    private static final String CRLF = "\r\n";
    private static final String PROTOCOL_VERSION = "HTTP/1.1 ";

    private HttpStatus statusCode;
    private byte[] body;
    private final Map<String, List<String>> headers = new LinkedHashMap<>();

    public Http11Response() {
    }

    public String getResponseHeader() {
        StringBuilder sb = new StringBuilder();

        // Status line
        sb.append(PROTOCOL_VERSION)
                .append(statusCode.getStatusCode())
                .append(" ")
                .append(statusCode.getStatusMessage())
                .append(" ")
                .append(CRLF);

        // Headers
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String value : entry.getValue()) {
                sb.append(entry.getKey()).append(": ").append(value).append(" ").append(CRLF);
            }
        }

        sb.append(CRLF);
        return sb.toString();
    }

    public byte[] getResponseBody() {
        return body;
    }

    public void status(HttpStatus statusCode) {
        this.statusCode = Objects.requireNonNull(statusCode, "statusCode required");
    }

    public void body(byte[] body) {
        this.body = body;
        int len = (body == null) ? 0 : body.length;
        setHeader("Content-Length", String.valueOf(len));
    }

    public void contentType(String contentType) {
        setHeader("Content-Type", contentType);
    }

    public void location(String location) {
        setHeader("Location", location);
    }

    public void cookie(String cookieValue) {
        addHeader("Set-Cookie", "JSESSIONID=" + cookieValue);
    }

    public void setHeader(String name, String value) {
        if (name != null && value != null) {
            headers.put(name, new ArrayList<>(List.of(value)));
        }
    }

    public void addHeader(String name, String value) {
        if (name != null && value != null) {
            List<String> values = headers.get(name);
            if (values == null) {
                values = new ArrayList<>();
                headers.put(name, values);
            }
            values.add(value);
        }
    }
}
