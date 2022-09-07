package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.HttpRequest;

public class ResponseProcessor {

    private final Response response;

    public ResponseProcessor(Response response) {
        this.response = response;
    }

    public static ResponseProcessor of(HttpRequest httpRequest, ResponseEntity responseEntity) {
        return new ResponseProcessor(Response.of(httpRequest, responseEntity));
    }

    public String getResponse() {
        return response.asString();
    }
}
