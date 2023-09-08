package org.apache.coyote.http11.httpresponse;

import org.apache.coyote.http11.Headers;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.http11.ResponseBody;

public class HttpResponseBuilder {

    private HttpVersion httpVersion;
    private HttpStatus httpStatus;
    private ResponseBody body;
    private Headers headers = new Headers();

    HttpResponseBuilder() {
    }

    public HttpResponseBuilder httpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
        return this;
    }

    public HttpResponseBuilder httpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpResponseBuilder body(ResponseBody body) {
        this.body = body;
        return this;
    }

    public HttpResponseBuilder header(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpResponseBuilder headers(Headers headers) {
        this.headers = headers;
        return this;
    }

    public HttpResponse build() {
        return new HttpResponse(httpVersion, httpStatus, body, headers);
    }
}
