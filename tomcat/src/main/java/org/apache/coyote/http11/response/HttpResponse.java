package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.List;

public class HttpResponse {

    private static final String LINE_SEPARATOR = "\r\n";
    private static final String EMPTY_BODY = "";

    private final StatusLine statusLine;
    private final ResponseHeaders headers;
    private final String body;

    private HttpResponse(final StatusLine statusLine, final ResponseHeaders headers, final String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(final HttpStatus status, final String contentType, final String body) {
        final ResponseHeaders headers = new ResponseHeaders();
        headers.setContentType(contentType);
        headers.setContentLength(body.getBytes().length);
        return new HttpResponse(StatusLine.from(status), headers, body);
    }

    public static HttpResponse redirect(final String location) {
        final ResponseHeaders headers = new ResponseHeaders();
        headers.setLocation(location);
        return new HttpResponse(StatusLine.from(HttpStatus.FOUND), headers, EMPTY_BODY);
    }

    public void setCookie(final String cookie) {
        headers.setCookie(cookie);
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