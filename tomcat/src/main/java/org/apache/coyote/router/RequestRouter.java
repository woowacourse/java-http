package org.apache.coyote.router;

import org.apache.coyote.cookie.HttpCookie;
import org.apache.coyote.render.PageRenderer;
import org.apache.coyote.handler.UserLoginHandler;
import org.apache.coyote.handler.UserRegisterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RequestRouter {

    private static final String LOGIN_REQUEST = "/login";
    private static final String REGISTER_REQUEST =  "/register";

    private static final Logger log = LoggerFactory.getLogger(RequestRouter.class);
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
        switch (path) {
            case LOGIN_REQUEST:
                return userLoginHandler.handle(method, path, formData,httpCookie);
            case  REGISTER_REQUEST:
                return userRegisterHandler.handle(method, path, formData, httpCookie);
            default:
                return pageRenderer.handle(method, path, httpCookie);
        }
    }
}
