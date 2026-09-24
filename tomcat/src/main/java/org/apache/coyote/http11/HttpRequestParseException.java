package org.apache.coyote.http11;

class HttpRequestParseException extends RuntimeException {

    HttpRequestParseException(String message) {
        super(message);
    }

    HttpRequestParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
