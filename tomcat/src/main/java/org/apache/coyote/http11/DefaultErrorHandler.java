package org.apache.coyote.http11;

import org.apache.coyote.model.response.StatusCode;

import static org.apache.coyote.model.response.HttpResponse.createResponse;
import static org.apache.coyote.model.response.HttpResponse.getResponseBody;

public class DefaultErrorHandler {

    private static final DefaultErrorHandler INSTANCE = new DefaultErrorHandler();
    public static final String NOT_FOUND_ERROR = "/404.html";

    private DefaultErrorHandler() {
    }

    public static DefaultErrorHandler getINSTANCE() {
        return INSTANCE;
    }

    public String getResponse() {
        return createResponse(StatusCode.NOT_FOUND, getResponseBody(NOT_FOUND_ERROR, getClass()))
                .getResponse();
    }
}
