package org.apache.coyote.http11;

public class HttpSpecException extends RuntimeException {

    protected HttpSpecException(String message) {
        super(message);
    }

    public static class HttpMethodException extends HttpSpecException {

        private static final String INVALID_HTTP_METHOD_MESSAGE = "잘못된 HTTP METHOD 요청입니다.";

        public HttpMethodException() {
            super(INVALID_HTTP_METHOD_MESSAGE);
        }
    }

    public static class HttpProtocolException extends HttpSpecException {

        private static final String INVALID_PROTOCOL_MESSAGE = "잘못된 네트워크 통신 프로토콜 요청입니다.";

        public HttpProtocolException() {
            super(INVALID_PROTOCOL_MESSAGE);
        }
    }
}
