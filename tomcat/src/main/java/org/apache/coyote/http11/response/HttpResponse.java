package org.apache.coyote.http11.response;

import static org.apache.coyote.Constants.CRLF;

import java.util.Map;
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

    public static HttpResponse found(String uri) {
        HttpResponseHeader header = new HttpResponseHeader(HttpStatus.FOUND)
                .addLocation(uri);
        return new HttpResponse(header, HttpResponseBody.empty());
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

    public HttpResponse addHeaders(Map<String, String> headers) {
        headers.entrySet().forEach(header::addHeader);
        return this;
    }
}
