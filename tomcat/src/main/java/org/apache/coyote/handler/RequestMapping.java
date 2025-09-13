package org.apache.coyote.handler;

import org.apache.coyote.dto.HttpRequest;
import org.apache.coyote.render.PageEndpoint;
import org.apache.coyote.render.PageRenderer;

public enum RequestMapping {
    INSTANCE;

    public Controller getController(HttpRequest request) {
        String path = request.path();

        if (PageEndpoint.LOGIN.getEndPoint().equals(path)) {
            return new UserLoginHandler();
        }
        if (PageEndpoint.REGISTER.getEndPoint().equals(path)) {
            return new UserRegisterHandler();
        }
        return new PageRenderer();
    }

    public static RequestMapping getInstance() {
        return INSTANCE;
    }
}