package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class HttpResponseBuilder {

    private HttpStatus httpStatus;

    private List<String> headers;

    private String body;

    public HttpResponseBuilder init() {
        this.httpStatus = null;
        this.headers = new ArrayList<>();
        this.body = null;
        return this;
    }

    public HttpResponseBuilder httpStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpResponseBuilder header(final String header) {
        this.headers.add(header);
        return this;
    }

    public HttpResponseBuilder body(final String body) {
        this.body = body;
        return this;
    }

    public HttpResponse build() {
        return new HttpResponse(httpStatus, headers, body);
    }

}
