package org.apache.coyote.router;

import org.apache.coyote.render.PageRenderer;
import org.apache.coyote.render.UserLoginHandler;
import org.apache.coyote.render.UserRegisterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RequestRouter {

    private static final String LOGIN_REQUEST = "loginRequest";
    private static final String STATIC_REQUEST =  "staticRequest";
    private static final String REGISTER_REQUEST =  "registerRequest";

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

    public String handleRoute(final String method,final  String path, final Map<String, String> formData) {
        final String requestType = determineRequestType(path,formData);

        switch (requestType) {
            case LOGIN_REQUEST:
                return userLoginHandler.handle(method, path, formData);
            case  REGISTER_REQUEST:
                return userRegisterHandler.handle(method, path, formData);
            default:
                return pageRenderer.handle(method, path);
        }
    }

    private String determineRequestType(final String path, final Map<String, String> formData) {
        if(!formData.isEmpty()){
            if (path.equals("/login")) {
                return LOGIN_REQUEST;
            }
            if (path.equals("/register")) {
                return REGISTER_REQUEST;
            }
        }
        return STATIC_REQUEST;
    }
}
