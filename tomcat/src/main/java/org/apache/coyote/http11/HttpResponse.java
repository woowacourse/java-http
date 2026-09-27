package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String PROTOCOL = "HTTP/1.1";
    private static final String LINE_SEPARATOR = "\r\n";
    private static final String STATIC_RESOURCE_ROOT = "static";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String HTML = "text/html;charset=utf-8";
    private static final String CSS = "text/css;charset=utf-8";
    private static final String JAVASCRIPT = "application/javascript;charset=utf-8";

    private HttpStatus status = HttpStatus.OK;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body = new byte[0];

    public void setStatus(final HttpStatus status) {
        this.status = status;
    }

    public void addHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public void addCookie(final String name, final String value) {
        addHeader(SET_COOKIE, name + "=" + value);
    }

    public void setBody(final String contentType, final String body) {
        addHeader(CONTENT_TYPE, contentType);
        this.body = body.getBytes(StandardCharsets.UTF_8);
    }

    public void sendRedirect(final String location) {
        this.status = HttpStatus.FOUND;
        addHeader(LOCATION, location);
    }

    public void sendStaticResource(final String path) throws IOException {
        final byte[] resource = readStaticResource(path);
        if (resource == null) {
            sendNotFound();
            return;
        }
        addHeader(CONTENT_TYPE, contentTypeOf(path));
        this.body = resource;
    }

    private void sendNotFound() throws IOException {
        this.status = HttpStatus.NOT_FOUND;
        final byte[] notFoundPage = readStaticResource("/404.html");
        if (notFoundPage != null) {
            addHeader(CONTENT_TYPE, HTML);
            this.body = notFoundPage;
        }
    }

    private byte[] readStaticResource(final String path) throws IOException {
        if (path.contains("..")) {
            return null;
        }
        try (final InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream(STATIC_RESOURCE_ROOT + path)) {
            if (inputStream == null) {
                return null;
            }
            return inputStream.readAllBytes();
        }
    }

    private String contentTypeOf(final String path) {
        if (path.endsWith(".css")) {
            return CSS;
        }
        if (path.endsWith(".js")) {
            return JAVASCRIPT;
        }
        return HTML;
    }

    public void write(final OutputStream outputStream) throws IOException {
        final StringBuilder response = new StringBuilder()
                .append(PROTOCOL).append(" ").append(status.getCode()).append(" ")
                .append(status.getReasonPhrase()).append(" ").append(LINE_SEPARATOR);

        headers.forEach((name, value) ->
                response.append(name).append(": ").append(value).append(" ").append(LINE_SEPARATOR));
        if (body.length > 0) {
            response.append(CONTENT_LENGTH).append(": ").append(body.length).append(" ").append(LINE_SEPARATOR);
        }
        response.append(LINE_SEPARATOR);

        outputStream.write(response.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }
}
