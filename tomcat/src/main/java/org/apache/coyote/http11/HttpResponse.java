package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class HttpResponse {

    private static final String CRLF = "\r\n";

    private StatusLine statusLine;
    private final Map<String, List<String>> headers = new LinkedHashMap<>();
    private byte[] body = new byte[0];

    public HttpResponse() {
        this.statusLine = new StatusLine(
                HttpVersion.HTTP_1_1,
                HttpStatus.OK
        );
        setHeader("Content-Length", "0 ");
    }

    public void setStatus(final HttpStatus status) {
        this.statusLine = new StatusLine(
                HttpVersion.HTTP_1_1,
                status
        );
    }

    public void addHeader(
            final String name,
            final String value
    ) {
        headers.computeIfAbsent(headerNameOf(name), ignored -> new ArrayList<>()).add(value);
    }

    public void setHeader(
            final String name,
            final String value
    ) {
        headers.put(headerNameOf(name), new ArrayList<>(List.of(value)));
    }

    public void setBody(final byte[] body) {
        this.body = body.clone();
        setHeader("Content-Length", body.length + " ");
    }

    public void sendRedirect(final String location) {
        setStatus(HttpStatus.FOUND);
        setHeader("Location", location);
        setBody(new byte[0]);
    }

    public boolean hasHeader(String name) {
        return headers.containsKey(headerNameOf(name));
    }

    private String headerNameOf(final String name) {
        for (String existingName : headers.keySet()) {
            if (existingName.equalsIgnoreCase(name)) {
                return existingName;
            }
        }
        return name;
    }

    public byte[] toByteArray() {
        var responseHead = new StringBuilder()
                .append(statusLine.serialize())
                .append(" ")
                .append(CRLF);

        headers.forEach((name, values) -> values.forEach(value -> responseHead
                .append(name)
                .append(": ")
                .append(value)
                .append(CRLF)));
        responseHead.append(CRLF);

        var response = new ByteArrayOutputStream();
        response.writeBytes(responseHead.toString().getBytes(StandardCharsets.UTF_8));
        response.writeBytes(body);
        return response.toByteArray();
    }
}
