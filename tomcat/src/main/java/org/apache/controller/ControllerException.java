package org.apache.controller;

import org.apache.coyote.http11.HttpMethod;

public class ControllerException extends RuntimeException{

    protected static final String HTTP_METHOD_DELIMITER = ": ";

    protected ControllerException(String message) {
        super(message);
    }

    public static class ControllerHttpMethodException extends ControllerException {

        private static final String INVALID_HTTP_METHOD_REQUEST_MESSAGE = "잘못된 HTTP METHOD 요청입니다.";

        public ControllerHttpMethodException(HttpMethod httpMethod) {
            super(INVALID_HTTP_METHOD_REQUEST_MESSAGE + HTTP_METHOD_DELIMITER + httpMethod.name());
        }
    }

    public static class ControllerNotImplementMethodException extends ControllerException {

        private static final String NOT_IMPLEMENT_METHOD_MESSAGE = "컨트롤러에서 해당 메소드를 구현하지 않았습니다.";

        public ControllerNotImplementMethodException(HttpMethod httpMethod) {
            super(NOT_IMPLEMENT_METHOD_MESSAGE + HTTP_METHOD_DELIMITER + httpMethod.name());
        }
    }
}
