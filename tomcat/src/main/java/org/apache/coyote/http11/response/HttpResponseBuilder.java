package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseBuilder {
    private final Map<String, String> responseHeaders = new HashMap<>();
    private HttpStatus httpStatus;
    private String messageBody;

    public HttpResponseBuilder withHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpResponseBuilder header(String key, String value) {
        this.responseHeaders.put(key, value);
        return this;
    }

    public HttpResponseBuilder headers(Map<String, String> headers) {
        this.responseHeaders.putAll(headers);
        return this;
    }

    public HttpResponseBuilder body(String messageBody) {
        this.messageBody = messageBody;
        return this;
    }

    public HttpResponse build() {
        return new HttpResponse(httpStatus, responseHeaders, messageBody);
    }
}
