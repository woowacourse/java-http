package org.apache.coyote.http11;

public class ResponseProcessor {

    private final Response response;

    public ResponseProcessor(Response response) {
        this.response = response;
    }

    public static ResponseProcessor of(ResponseEntity responseEntity) {
        return new ResponseProcessor(Response.of(responseEntity));
    }

    public String getResponse() {
        return response.asString();
    }
}
