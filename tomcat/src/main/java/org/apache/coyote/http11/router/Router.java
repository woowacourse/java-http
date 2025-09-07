package org.apache.coyote.http11.router;

import java.util.Objects;
import org.apache.coyote.http11.dto.HttpRequest;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handler.LoginHandler;
import org.apache.coyote.http11.handler.StaticFileHandler;

public class Router {

    private final StaticFileHandler staticFileHandler;
    private final LoginHandler loginHandler;

    public Router(final StaticFileHandler staticFileHandler) {
        this.staticFileHandler = staticFileHandler;
        this.loginHandler = new LoginHandler(staticFileHandler);
    }

    public Handler route(final HttpRequest request) {
        if (Objects.equals(request.route(), "/login")) {
            return loginHandler;
        }
        return staticFileHandler;
    }
}
