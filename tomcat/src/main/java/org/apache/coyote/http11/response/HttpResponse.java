package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.HttpHeaders;

public class HttpResponse {

    private final StatusLine statusLine;
    private final HttpHeaders headers;
    private final String body;

    private HttpResponse(StatusLine statusLine, HttpHeaders headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse of(String httpVersion, HttpStatus httpStatus, String body) {
        StatusLine statusLine = new StatusLine(httpVersion, httpStatus);
        return new HttpResponse(statusLine, HttpHeaders.empty(), body);
    }

    public void addHeader(String key, String value) {
        headers.add(key, value);
    }

    public HttpStatus httpStatus() {
        return statusLine.getHttpStatus();
    }

    public String getBody() {
        return body;
    }

    public String format() {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(statusLine.format());
        responseBuilder.append(headers.format()).append("\r\n");
        responseBuilder.append(body);
        return responseBuilder.toString();
    }
}
