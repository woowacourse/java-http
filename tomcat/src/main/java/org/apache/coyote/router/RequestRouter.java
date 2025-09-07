package org.apache.coyote.router;

import org.apache.coyote.render.UserApiProcessor;
import org.apache.coyote.render.PageRenderer;
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
