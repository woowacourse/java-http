package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.HttpRequest;

public class ResponseProcessor {

    private final HttpResponse httpResponse;

    public ResponseProcessor(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public static ResponseProcessor of(HttpRequest httpRequest, ResponseEntity responseEntity) {
        return new ResponseProcessor(HttpResponse.of(httpRequest, responseEntity));
    }

    public String getResponse() {
        return httpResponse.asString();
    }
}
