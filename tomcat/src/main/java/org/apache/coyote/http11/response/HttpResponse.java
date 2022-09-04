package org.apache.coyote.http11.response;

import static org.apache.coyote.Constants.CRLF;

import org.apache.coyote.http11.response.element.HttpStatus;

public class HttpResponse {

    private final HttpResponseHeader header;
    private final HttpResponseBody body;

    public HttpResponse(HttpResponseHeader header, HttpResponseBody body) {
        this.header = header;
        this.body = body;
    }

    public static HttpResponse from(HttpResponseBody responseBody, HttpStatus status, String contentType) {
        HttpResponseHeader header = new HttpResponseHeader(status)
                .addContentType(contentType)
                .addContentLength(responseBody.getContentLength());
        return new HttpResponse(header, responseBody);
    }

    public HttpResponseBody getBody() {
        return body;
    }

    public String getResponse() {
        return String.join(CRLF,
                header.getHeaders(),
                "",
                body.getBodyContext());
    }
}
