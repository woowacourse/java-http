package org.apache.coyote.http;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HttpResponse {

    private static final String CRLF = "\r\n";

    private final String version;
    private final HttpStatus status;
    private final ContentType type;
    private final String body;
    private final Map<String, String> headers;
    private final Map<String, String> cookies;

    public HttpResponse(final String version, final HttpStatus status, final ContentType type, final String body) {
        this(version, status, type, body, new HashMap<>(), new HashMap<>());
    }

    public static HttpResponse redirect(final String version, final String location) {
        final Map<String, String> headers = new HashMap<>();
        headers.put("Location", location);
        return new HttpResponse(version, HttpStatus.FOUND, ContentType.HTML, "", headers, new HashMap<>());
    }

    public void setCookie(final String name, final String value) {
        cookies.put(name, value);
    }

    @Override
    public String toString() {
        final StringBuilder response = new StringBuilder();

        appendStatusLine(response);
        appendContentHeaders(response);
        appendCustomHeaders(response);
        appendCookies(response);
        appendBody(response);

        return response.toString();
    }

    private void appendStatusLine(final StringBuilder response) {
        response.append("HTTP/").append(version).append(" ")
                .append(status.getCode()).append(" ")
                .append(status.getReasonPhrase()).append(CRLF);
    }

    private void appendContentHeaders(final StringBuilder response) {
        final int contentLength = body == null ? 0 : body.getBytes(StandardCharsets.UTF_8).length;

        response.append("Content-Type: ").append(type.getMimeType()).append(CRLF)
                .append("Content-Length: ").append(contentLength).append(CRLF);
    }

    private void appendCustomHeaders(final StringBuilder response) {
        headers.forEach((key, value) ->
                response.append(key).append(": ").append(value).append(CRLF));
    }

    private void appendCookies(final StringBuilder response) {
        cookies.forEach((name, value) ->
                response.append("Set-Cookie: ").append(name).append("=").append(value).append(CRLF));
    }

    private void appendBody(final StringBuilder response) {
        final String bodyContent = body == null ? "" : body;
        response.append(CRLF)
                .append(bodyContent);
    }
}
