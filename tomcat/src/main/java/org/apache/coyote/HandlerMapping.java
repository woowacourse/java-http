package org.apache.coyote;

import org.apache.coyote.handler.Handler;
import org.apache.coyote.handler.LoginHandler;
import org.apache.coyote.handler.RegisterHandler;
import org.apache.coyote.handler.RootEndPointHandler;
import org.apache.coyote.handler.StaticResourceHandler;
import org.apache.http.request.HttpRequest;

public class HandlerMapping {
    private static final String PATH_DELIMITER = "/";

    public Handler getHandler(final HttpRequest httpRequest) {
        final String path = httpRequest.getPath();

        if (path.equals(PATH_DELIMITER)) {
            return RootEndPointHandler.getInstance();
        }

        if (path.contains("login")) {
            return LoginHandler.getInstance();
        }

        if (path.contains("register")) {
            return RegisterHandler.getInstance();
        }

        return StaticResourceHandler.getInstance();
    }
}
