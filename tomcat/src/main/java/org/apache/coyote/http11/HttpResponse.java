package org.apache.coyote.http11;

import org.apache.coyote.http11.enums.HttpStatus;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final HttpRequest httpRequest;
    private final HttpHeaders httpHeaders;
    private final String body;

    public HttpResponse(final HttpStatus httpStatus, final HttpRequest httpRequest, final String body) {
        this.httpStatus = httpStatus;
        this.httpRequest = httpRequest;
        this.body = body;
        this.httpHeaders = initHeaders(httpRequest, body);
    }

    private HttpHeaders initHeaders(final HttpRequest httpRequest, final String body) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.addValue("Content-Type", httpRequest.findContentType().getValue() + ";charset=utf-8");
        httpHeaders.addValue("Content-Length", body.getBytes().length);
        return httpHeaders;
    }

    public String generateResponse() {
        final String separator = "\r\n";
        return String.join(separator,
                generateStatusLine(),
                httpHeaders.generate(separator),
                body);
    }

    private String generateStatusLine() {
        return "HTTP/1.1 " + httpStatus.getValue() + " OK ";
    }
}
