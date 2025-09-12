package org.apache.coyote.http11.request_response.response;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.http11.request_response.HttpStatus;
import org.apache.coyote.http11.request_response.HttpHeader;

public class HttpResponseBuilder {
    private HttpStatus httpStatus;
    private String responseBody;
    private List<HttpHeader> headers = new ArrayList<>();

    public HttpResponseBuilder() {
    }

    public HttpResponseBuilder header(String name, String value) {
        headers.add(new HttpHeader(name, value));
        return this;
    }

    public HttpResponseBuilder header(String name, int value) {
        headers.add(new HttpHeader(name, String.valueOf(value)));
        return this;
    }

    public HttpResponseBuilder status(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpResponseBuilder contentType(String value) {
        headers.add(new HttpHeader("Content-Type", value));
        return this;
    }

    public HttpResponseBuilder cookie(String name, String value) {
        headers.add(new HttpHeader("Set-Cookie", "%s=%s;".formatted(name, value)));
        return this;
    }

    public HttpResponseBuilder body(String responseBody) {
        this.responseBody = responseBody;
        header("Content-Length", responseBody.getBytes(StandardCharsets.UTF_8).length);
        return this;
    }

    public HttpResponse build() {
        return new HttpResponse(httpStatus, headers, responseBody);
    }
}
