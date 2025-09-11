package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Http11Response {

    private static final String CRLF = "\r\n";
    private static final String PROTOCOL_VERSION = "HTTP/1.1 ";

    private HttpStatus statusCode;
    private byte[] body;
    private final Map<String, String> headers = new LinkedHashMap<>();

    public Http11Response() {
    }

    public String getResponseHeader() {
        StringBuilder sb = new StringBuilder();

        sb.append(PROTOCOL_VERSION)
                .append(statusCode.getStatusCode())
                .append(" ")
                .append(statusCode.getStatusMessage())
                .append(" ")
                .append(CRLF);

        for (Map.Entry<String, String> e : headers.entrySet()) {
            sb.append(e.getKey()).append(": ").append(e.getValue()).append(" ").append(CRLF);
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
        this.headers.put("Content-Length", String.valueOf(len));
    }

    public void contentType(String contentType) {
        header("Content-Type", contentType);
    }

    public void location(String location) {
        header("Location", location);
    }

    public void cookie(String cookieValue) {
        header("Set-Cookie", "JSESSIONID=" + cookieValue);
    }

    public void header(String name, String value) {
        if (name != null && value != null) {
            headers.put(name, value);
        }
    }
}
