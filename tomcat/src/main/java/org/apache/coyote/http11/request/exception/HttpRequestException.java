package org.apache.coyote.http11.request.exception;

public class HttpRequestException extends RuntimeException {

    public HttpRequestException(final String message) {
        super(message);
    }

    public static class MethodNotAllowed extends HttpRequestException {

        public MethodNotAllowed() {
            super("허용되지 않는 HTTP Method 입니다.");
        }
    }
}
