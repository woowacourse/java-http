package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final byte[] EMPTY_BODY = new byte[0];

    private final OutputStream outputStream;
    private final Map<String, String> headers = new LinkedHashMap<>();

    private HttpStatus status = HttpStatus.OK;
    private byte[] body = EMPTY_BODY;
    private boolean sent;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = Objects.requireNonNull(outputStream);
    }

    public void setStatus(HttpStatus status) {
        this.status = Objects.requireNonNull(status);
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setContentType(String contentType) {
        setHeader("Content-Type", contentType);
    }

    public void setBody(String body) {
        setBody(body.getBytes(StandardCharsets.UTF_8));
    }

    public void setBody(byte[] body) {
        this.body = body.clone();
    }

    public void redirect(String location) {
        setStatus(HttpStatus.FOUND);
        setHeader("Location", location);
        setBody(EMPTY_BODY);
    }

    public void addCookie(String name, String value) {
        setHeader("Set-Cookie", name + "=" + value);
    }

    public void reset(HttpStatus status) {
        setStatus(status);
        headers.clear();
        setBody(EMPTY_BODY);
    }

    public void send() throws IOException {
        if (sent) {
            return;
        }

        headers.put("Content-Length", String.valueOf(body.length));
        outputStream.write(createHead().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
        sent = true;
    }

    private String createHead() {
        StringBuilder responseHead = new StringBuilder();
        responseHead.append(new StatusLine(HTTP_VERSION, status)).append("\r\n");
        headers.forEach((name, value) -> responseHead
                .append(name)
                .append(": ")
                .append(value)
                .append("\r\n"));
        return responseHead.append("\r\n").toString();
    }
}
