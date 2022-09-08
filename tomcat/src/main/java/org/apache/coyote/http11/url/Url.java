package org.apache.coyote.http11.url;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Url {
    protected static final Logger log = LoggerFactory.getLogger(Register.class);

    protected final HttpRequest request;

    protected Url(HttpRequest request) {
        this.request = request;
    }

    public abstract HttpResponse handle(HttpHeaders httpHeaders, String requestBody);

    public String getPath() {
        return request.getPath();
    }

    public HttpRequest getRequest() {
        return request;
    }
}
