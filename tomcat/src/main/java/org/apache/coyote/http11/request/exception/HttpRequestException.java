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

    public static class NotMatchSession extends HttpRequestException {

        public NotMatchSession() {
            super("SessionId와 일치하는 세션이 존재하지 않습니다.");
        }
    }

    public static class NotFoundMappingController extends HttpRequestException {

        public NotFoundMappingController() {
            super("Request URI에 매핑되는 Controller를 찾을 수 없습니다.");
        }
    }
}
