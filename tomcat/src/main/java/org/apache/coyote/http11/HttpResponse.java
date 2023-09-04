package org.apache.coyote.http11;

import nextstep.jwp.controller.ResponseEntity;

public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String EMPTY_STRING = "";
    private final HttpHeaders headers;
    private HttpResponseStatusLine responseLine;
    private String body;

    public HttpResponse(final HttpResponseStatusLine responseLine, final HttpHeaders headers, final String body) {
        this.responseLine = responseLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse(final HttpResponseStatusLine responseLine, final String body) {
        this(responseLine, new HttpHeaders(), body);
    }

    public static HttpResponse createBasicResponse() {
        return new HttpResponse(HttpResponseStatusLine.OK(), EMPTY_STRING);
    }

    public void addAttributes(final ResponseEntity responseBody) {
        headers.addHeader(responseBody.getHeaders());
        final int bodyLength = responseBody.getBody().getBytes().length;
        headers.addHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(bodyLength));
        responseLine = responseBody.getResponseLine();
        body = responseBody.getBody();
    }

    public void sendRedirect(final StaticPages staticPages) {
        headers.addHeader(HttpHeaderName.LOCATION, staticPages.getFileName());
        responseLine = HttpResponseStatusLine.FOUND();
    }

    public HttpResponseStatusLine getResponseLine() {
        return responseLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }

    @Override
    public String toString() {
        return String.join(CRLF, responseLine.toString(), headers.toString(), body);
    }
}
