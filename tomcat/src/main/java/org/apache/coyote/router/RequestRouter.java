package org.apache.coyote.router;

import org.apache.coyote.dto.HttpRequest;
import org.apache.coyote.handler.UserLoginHandler;
import org.apache.coyote.handler.UserRegisterHandler;
import org.apache.coyote.render.PageRenderer;

public class RequestRouter {

    private static final String LOGIN_REQUEST = "/login";
    private static final String REGISTER_REQUEST =  "/register";

    private final UserLoginHandler userLoginHandler;
    private final UserRegisterHandler userRegisterHandler;
    private final PageRenderer pageRenderer;

    public RequestRouter(
            final UserLoginHandler userLoginHandler,
            final UserRegisterHandler userRegisterHandler,
            final PageRenderer pageRenderer
    ) {
        this.userLoginHandler = userLoginHandler;
        this.userRegisterHandler = userRegisterHandler;
        this.pageRenderer = pageRenderer;
    }

    public String handleRoute(final HttpRequest httpRequest) {
        return switch (httpRequest.path()) {
            case LOGIN_REQUEST -> userLoginHandler.handle(httpRequest);
            case REGISTER_REQUEST -> userRegisterHandler.handle(httpRequest);
            default -> pageRenderer.handle(httpRequest);
        };
    }
}
