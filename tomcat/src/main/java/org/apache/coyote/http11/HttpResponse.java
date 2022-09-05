package org.apache.coyote.http11;

import java.util.Map.Entry;

public class HttpResponse {

    private final String httpVersion;
    private HttpStatus httpStatus;
    private final Headers headers;
    private String body;

    public HttpResponse() {
        this("HTTP/1.1", HttpStatus.OK, new Headers(), "");
    }

    private HttpResponse(final String httpVersion, final HttpStatus httpStatus, final Headers headers,
                         final String body) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse httpStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpResponse addHeader(final String key, final String value) {
        headers.add(key, value);
        return this;
    }

    public HttpResponse body(final String body, final MediaType mediaType) {
        this.body = body;
        addHeader("Content-Type", mediaType.getValue());
        addHeader("Content-Length", Integer.toString(this.body.getBytes().length));
        return this;
    }

    public String getValue() {
        return String.join("\r\n", toStartLine(), toHeaders(), body);
    }

    private String toStartLine() {
        return String.join(" ", httpVersion, httpStatus.getCode(), httpStatus.getMessage(), "");
    }

    private String toHeaders() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, String> header : headers.getValue().entrySet()) {
            stringBuilder.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append(" \r\n");
        }
        return stringBuilder.toString();
    }
}
