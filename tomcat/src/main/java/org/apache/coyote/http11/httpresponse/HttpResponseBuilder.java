package org.apache.coyote.http11.httpresponse;

import org.apache.coyote.http11.Headers;
import org.apache.coyote.http11.HttpCookie;
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

    public HttpResponseBuilder setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
        return this;
    }

    public HttpResponseBuilder setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpResponseBuilder setBody(ResponseBody body) {
        this.body = body;
        return this;
    }

    public HttpResponseBuilder addCookie(HttpCookie httpCookie) {
        headers.put("Set-Cookie", httpCookie.toString());
        return this;
    }

    public HttpResponseBuilder sendRedirect(String location) {
        headers.put("Location", location);
        return this;
    }

    public HttpResponse build() {
        return new HttpResponse(httpVersion, httpStatus, body, headers);
    }

}
