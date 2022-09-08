package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpHeader.LOCATION;

public class HttpResponse {

    private static final String PROTOCOL = "HTTP/1.1";

    private HttpStatusCode statusCode;
    private final HttpHeaders httpHeaders;
    private String body;

    public HttpResponse() {
        this.httpHeaders = new HttpHeaders();
        this.body = "";
    }

    public HttpResponse statusCode(final HttpStatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpResponse addHeader(final HttpHeader header, final String value) {
        httpHeaders.addHeader(header, value);
        return this;
    }

    public HttpResponse addHeader(final HttpHeader header, final int value) {
        httpHeaders.addHeader(header, String.valueOf(value));
        return this;
    }

    public HttpResponse addCookie(final HttpCookie httpCookie) {
        httpHeaders.addCookie(httpCookie);
        return this;
    }

    public HttpResponse responseBody(final String responseBody) {
        this.body = responseBody;
        return this;
    }

    public HttpResponse redirect(final String url) {
        httpHeaders.addHeader(LOCATION, url);
        return this;
    }

    public byte[] getBytes() {
        return encodingResponse().getBytes();
    }

    private String encodingResponse() {
        if (body.isBlank()) {
            return String.join("\r\n",
                    PROTOCOL + " " + statusCode.toHttpString() + " ",
                    httpHeaders.encodingToString(),
                    "");
        }
        return String.join("\r\n",
                PROTOCOL + " " + statusCode.toHttpString() + " ",
                httpHeaders.encodingToString(),
                "",
                body);
    }
}
