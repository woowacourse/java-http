package org.apache.coyote.http11.http.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.cookie.Cookie;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.http.HttpVersion;

public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String SP = " ";

    private final String version;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private final ByteArrayOutputStream body = new ByteArrayOutputStream();
    private boolean committed = false;
    private String message = "OK";
    private int status = 200;
    private boolean redirected = false;

    public HttpResponse(HttpVersion version) {
        this.version = version.toString();
        headers.put("Connection", "close");
    }

    public HttpResponse status(HttpStatus httpStatus) {
        ensureNotCommitted();
        this.status = httpStatus.getCode();
        this.message = httpStatus.getMessage();
        return this;
    }

    public HttpResponse header(String name, String value) {
        ensureNotCommitted();
        headers.put(name, value);
        return this;
    }

    public HttpResponse contentType(String contentType) {
        return header("Content-Type", contentType);
    }

    public HttpResponse cookie(Cookie cookie) {
        ensureNotCommitted();
        return header("Set-Cookie", cookie.toHeaderValue());
    }

    public HttpResponse write(String text) throws IOException {
        if (redirected) {
            throw new IllegalStateException("Redirect response cannot cannot have a body");
        }
        body.write(text.getBytes(StandardCharsets.UTF_8));
        return this;
    }

    public HttpResponse write(byte[] bytes) throws IOException {
        if (redirected) {
            throw new IllegalStateException("Redirect response cannot cannot have a body");
        }
        body.write(bytes);
        return this;
    }

    public void sendRedirect(String location) {
        this.redirected = true;
        status(HttpStatus.FOUND);
        header("Location", location);
        headers.put("Content-Length", "0");
    }

    public void commit(OutputStream out) throws IOException {
        if (committed) {
            return;
        }
        committed = true;

        byte[] bodyBytes = body.toByteArray();
        headers.putIfAbsent("Content-Length", String.valueOf(bodyBytes.length));

        StringBuilder sb = new StringBuilder();
        sb.append(version).append(" ").append(status).append(SP).append(message).append(CRLF);
        for (var entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append(CRLF);
        }
        sb.append(CRLF);

        out.write(sb.toString().getBytes(StandardCharsets.ISO_8859_1));
        if (bodyBytes.length > 0 && status != HttpStatus.FOUND.getCode()) {
            out.write(bodyBytes);
        }
        out.flush();
    }

    private void ensureNotCommitted() {
        if (committed) {
            throw new IllegalStateException("Response already committed");
        }
    }
}
