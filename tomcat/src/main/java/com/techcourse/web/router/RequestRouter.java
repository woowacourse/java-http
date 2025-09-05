package com.techcourse.web.router;

import com.techcourse.web.api.UserApiProcessor;
import com.techcourse.web.ui.PageRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestRouter {
    private static final Logger log = LoggerFactory.getLogger(RequestRouter.class);
    private final UserApiProcessor userApiProcessor;
    private final PageRenderer pageRenderer;

    public RequestRouter() {
        this.userApiProcessor = new UserApiProcessor();
        this.pageRenderer = new PageRenderer();
    }

    public String handleRoute(String method, String path) {
        if (isApiPath(path)) {
            return userApiProcessor.handle(method, path);
        }
        return pageRenderer.handle(method, path);
    }

    private boolean isApiPath(String path) {
        return path.startsWith("/login?");
    }
}
