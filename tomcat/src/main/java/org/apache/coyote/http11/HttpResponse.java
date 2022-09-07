package org.apache.coyote.http11;

import java.util.Collections;

public class HttpResponse {

    private StatusLine statusLine;
    private final HttpHeaders httpHeaders;
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

    public String getViewName() {
        return viewName;
    }
}
