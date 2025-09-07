package org.apache.coyote.router;

import org.apache.coyote.render.PageRenderer;
import org.apache.coyote.render.UserLoginProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RequestRouter {

    private static final String LOGIN_REQUEST = "loginRequest";
    private static final String STATIC_REQUEST =  "staticRequest";

    private static final Logger log = LoggerFactory.getLogger(RequestRouter.class);
    private final UserLoginProcessor userLoginProcessor;
    private final PageRenderer pageRenderer;

    public RequestRouter() {
        this.userLoginProcessor = new UserLoginProcessor();
        this.pageRenderer = new PageRenderer();
    }

    public String handleRoute(String method, String path, final Map<String, String> queryParams) {
        String requestType = determineRequestType(path);

        switch (requestType) {
            case LOGIN_REQUEST:
                return userLoginProcessor.handle(method, path, queryParams);
            default:
                return pageRenderer.handle(method, path);
        }
    }

    private String determineRequestType(String path) {
        String cleanPath = path.contains("?") ? path.split("\\?")[0] : path;
        if (cleanPath.startsWith("/login?")) {
            return LOGIN_REQUEST;
        }
        return STATIC_REQUEST;
    }
}
