package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final String PROTOCOL = "HTTP/1.1";
    private static final String CRLF = "\r\n";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> headers = new LinkedHashMap<>();

    private HttpStatus status = HttpStatus.OK;
    private String body = "";

    public void setStatus(final HttpStatus status) {
        this.status = status;
    }

    public void setContentType(final ContentType contentType) {
        headers.put(CONTENT_TYPE, contentType.getValue());
    }

    public void setBody(final String body) {
        this.body = body;
    }

    public void ok(final ContentType contentType, final String body) {
        this.status = HttpStatus.OK;
        this.body = body;
        setContentType(contentType);
    }

    public void okHtml(final String body) {
        ok(ContentType.HTML, body);
    }

    public void sendError(final HttpStatus status, final String body) {
        this.status = status;
        this.body = body;
        setContentType(ContentType.HTML);
    }

    public void sendRedirect(final String location) {
        this.status = HttpStatus.FOUND;
        this.body = "";
        headers.put(LOCATION, location);
    }

    public void addCookie(final String name, final String value) {
        headers.put(SET_COOKIE, name + "=" + value);
    }

    public void addJSessionId(final String sessionId) {
        addCookie(JSESSIONID, sessionId);
    }

    public String getResponse() {
        final Map<String, String> responseHeaders = new LinkedHashMap<>(headers);
        responseHeaders.put(CONTENT_LENGTH, String.valueOf(body.getBytes().length));

        final String headerLines = responseHeaders.entrySet().stream()
                .map(header -> header.getKey() + ": " + header.getValue() + " ")
                .collect(Collectors.joining(CRLF));

        return String.join(CRLF,
                PROTOCOL + " " + status.getCode() + " " + status.getResponsePhrase() + " ",
                headerLines,
                "",
                body);
    }
}
