package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.Request;

public class ResponseProcessor {

    private final Response response;

    public ResponseProcessor(Response response) {
        this.response = response;
    }

    public static ResponseProcessor of(Request request, ResponseEntity responseEntity) {
        return new ResponseProcessor(Response.of(request, responseEntity));
    }

    public String getResponse() {
        return response.asString();
    }
}
