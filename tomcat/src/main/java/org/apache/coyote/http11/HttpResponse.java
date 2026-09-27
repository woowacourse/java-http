package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private static final String PROTOCOL_VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String LOCATION_HEADER = "Location";

    private HttpStatus httpStatus = HttpStatus.OK;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body = new byte[0];

    public void setStatus(final HttpStatus status) {
        this.httpStatus = status;
    }

    public void setHeader(final String name, final String value) {
        this.headers.put(name, value);
    }

    public void setBody(final byte[] body) {
        this.body = body;
        setHeader(CONTENT_LENGTH_HEADER, String.valueOf(body.length));
    }

    public void sendRedirect(final String location) {
        setStatus(HttpStatus.FOUND);
        setHeader(LOCATION_HEADER, location);
    }

    @Override
    public String toString() {
        return "HttpResponse {" + "\r\n" +
                "status= " + httpStatus.getCode() + " " + httpStatus.getReasonPhrase() + "\r\n" +
                "headers= " + headers + "\r\n" +
                "bodyLength= " + body.length + "\r\n" +
                "}";
    }

    public String build() {
        final List<String> response = new ArrayList<>();
        response.add(PROTOCOL_VERSION + " " + httpStatus.getCode() + " " + httpStatus.getReasonPhrase());
        for (var entry : headers.entrySet()) {
            response.add(entry.getKey() + ": " + entry.getValue());
        }
        response.add("");
        response.add(new String(body, StandardCharsets.UTF_8));
        return String.join(CRLF, response);
    }
}
