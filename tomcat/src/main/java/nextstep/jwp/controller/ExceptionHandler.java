package nextstep.jwp.controller;

import static common.ResponseStatus.NOT_FOUND;
import static common.ResponseStatus.UNAUTHORIZED;

import nextstep.jwp.exception.AuthException;
import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.response.HttpResponse;

public class ExceptionHandler {

    private static final String UNAUTHORIZED_URL = "/401.html";
    private static final String NOT_FOUND_URL = "/404.html";

    public void handle(HttpResponse httpResponse, Exception e) {
        if (e instanceof AuthException) {
            ResourceManager manager = ResourceManager.from(UNAUTHORIZED_URL);
            httpResponse.setResponseResource(
                    UNAUTHORIZED,
                    manager.extractResourceType(),
                    manager.readResourceContent()
            );
            return;
        }
        if (e instanceof NotFoundException) {
            ResourceManager manager = ResourceManager.from(NOT_FOUND_URL);
            httpResponse.setResponseResource(
                    NOT_FOUND,
                    manager.extractResourceType(),
                    manager.readResourceContent()
            );
        }
    }
}
