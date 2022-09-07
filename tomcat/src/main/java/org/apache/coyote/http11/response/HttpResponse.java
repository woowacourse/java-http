package org.apache.coyote.http11.response;

import java.util.Collections;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpHeaders;

public class HttpResponse {

    private final HttpHeaders httpHeaders;
    private StatusLine statusLine;
    private String body;
    private String viewName;

    public HttpResponse() {
        statusLine = new StatusLine(HttpStatus.OK);
        httpHeaders = new HttpHeaders(Collections.emptyMap());
        body = "";
        viewName = null;
    }

    public String makeResponse() {
        StringBuilder sb = new StringBuilder();

        sb.append(statusLine.getStatusLine()).append(" \r\n")
                .append(httpHeaders.toTextHeader()).append("\r\n")
                .append(body);

        return sb.toString();
    }

    public void sendRedirect(final String location) {
        setStatus(HttpStatus.FOUND);
        httpHeaders.setLocation(location);
    }

    public Optional<String> getViewName() {
        if (viewName == null) {
            return Optional.empty();
        }

        return Optional.of(viewName);
    }

    public HttpStatus getHttpStatus() {
        return statusLine.getHttpStatus();
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public void setStatus(final HttpStatus httpStatus) {
        this.statusLine = new StatusLine(httpStatus);
    }

    public void setBody(final String value) {
        body += value;
        httpHeaders.setContentType("text/html");
        httpHeaders.setContentLength(body.length());
    }

    public void setBody(final String value, final String contentType, final int contentLength) {
        body += value;
        httpHeaders.setContentType(contentType);
        httpHeaders.setContentLength(contentLength);
    }

    public void setView(final String viewName) {
        this.viewName = viewName;
    }
}
