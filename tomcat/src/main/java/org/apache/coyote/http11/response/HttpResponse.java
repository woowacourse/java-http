package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.List;

public class HttpResponse {

    private static final String LINE_SEPARATOR = "\r\n";
    private static final String EMPTY_BODY = "";

    private StatusLine statusLine = StatusLine.from(HttpStatus.OK);
    private final ResponseHeaders headers = new ResponseHeaders();
    private String body = EMPTY_BODY;

    public void setStatus(final HttpStatus status) {
        this.statusLine = StatusLine.from(status);
    }

    public void setBody(final String contentType, final String body) {
        this.body = body;
        headers.setContentType(contentType);
        headers.setContentLength(body.getBytes().length);
    }

    public void sendRedirect(final String location) {
        setStatus(HttpStatus.FOUND);
        headers.setLocation(location);
    }

    public void setCookie(final String cookie) {
        headers.setCookie(cookie);
    }

    public void setHeader(final String name, final String value) {
        headers.setHeader(name, value);
    }

    public String toMessage() {
        final List<String> lines = new ArrayList<>();
        lines.add(statusLine.toString());
        lines.addAll(headers.toLines());
        lines.add(EMPTY_BODY);
        lines.add(body);
        return String.join(LINE_SEPARATOR, lines);
    }
}
