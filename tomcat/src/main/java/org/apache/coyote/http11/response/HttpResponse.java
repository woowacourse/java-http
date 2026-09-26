package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private static final String PROTOCOL = "HTTP/1.1";
    private static final String CRLF = "\r\n";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> headers = new LinkedHashMap<>();
    private final List<String> cookies = new ArrayList<>();

    private HttpStatus status = HttpStatus.OK;
    private String body = "";

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
        cookies.add(name + "=" + value);
    }

    public void addJSessionId(final String sessionId) {
        addCookie(JSESSIONID, sessionId);
    }

    public String getResponse() {
        final List<String> headerLines = new ArrayList<>();
        headers.forEach((name, value) -> headerLines.add(name + ": " + value + " "));
        cookies.forEach(cookie -> headerLines.add(SET_COOKIE + ": " + cookie + " "));
        headerLines.add(CONTENT_LENGTH + ": " + body.getBytes().length + " ");

        return String.join(CRLF,
                PROTOCOL + " " + status.getCode() + " " + status.getResponsePhrase() + " ",
                String.join(CRLF, headerLines),
                "",
                body);
    }
}
