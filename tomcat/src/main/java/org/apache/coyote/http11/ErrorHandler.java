package org.apache.coyote.http11;

import org.apache.coyote.model.response.StatusCode;

import static org.apache.coyote.model.response.HttpResponse.createResponse;

public class ErrorHandler {

    private static final ErrorHandler INSTANCE = new ErrorHandler();

    private ErrorHandler() {
    }

    public static ErrorHandler getINSTANCE() {
        return INSTANCE;
    }

    public String getResponse(StatusCode statusCode, String message) {
        return createResponse(statusCode, message).getResponse();
    }
}
