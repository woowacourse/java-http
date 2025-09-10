package org.apache.coyote.router;

import java.util.Map;
import org.apache.coyote.cookie.HttpCookie;
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

    public String handleRoute(
            final String method,
            final  String path,
            final Map<String, String> formData,
            final HttpCookie httpCookie
    ) {
        return switch (path) {
            case LOGIN_REQUEST -> userLoginHandler.handle(method, path, formData, httpCookie);
            case REGISTER_REQUEST -> userRegisterHandler.handle(method, path, formData, httpCookie);
            default -> pageRenderer.handle(method, path, httpCookie);
        };
    }
}
