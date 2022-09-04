package org.apache.coyote.http11;

public class ResponseEntity {

    private final String responseBody;
    private HttpStatus httpStatus;

    private ResponseEntity(ResponseEntityBuilder builder) {
        this.responseBody = builder.responseBody;
        this.httpStatus = builder.httpStatus;
    }

    public ResponseEntity status(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public String getResponseBody() {
        return this.responseBody;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public static ResponseEntity body(String body) {
        return new ResponseEntityBuilder().body(body);
    }

    private static class ResponseEntityBuilder {

        private String responseBody;
        private HttpStatus httpStatus = HttpStatus.OK;

        public ResponseEntity body(String body) {
            this.responseBody = body;
            return new ResponseEntity(this);
        }
    }
}
