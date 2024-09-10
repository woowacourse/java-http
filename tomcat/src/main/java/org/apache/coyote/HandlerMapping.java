package org.apache.coyote;

import org.apache.coyote.handler.Handler;
import org.apache.coyote.handler.LoginHandler;
import org.apache.coyote.handler.RegisterHandler;
import org.apache.coyote.handler.RootEndPointHandler;
import org.apache.coyote.handler.StaticResourceHandler;
import org.apache.coyote.handler.exception.InternalServerErrorHandler;
import org.apache.coyote.handler.exception.NotFoundHandler;
import org.apache.coyote.handler.exception.UnAuthorizationHandler;
import org.apache.http.request.HttpRequest;


public class HandlerMapping {

    private static final HandlerMapping INSTANCE = new HandlerMapping();
    private static final String PATH_DELIMITER = "/";

    private HandlerMapping() {
    }

    public static HandlerMapping getInstance() {
        return INSTANCE;
    }

    public Handler getHandler(final HttpRequest httpRequest) {
        return getHandlerByEndPoint(httpRequest);
    }

    private Handler getHandlerByEndPoint(final HttpRequest httpRequest) {
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

    public Handler getHandlerByException(final Exception exception) {
        if (exception instanceof NotFoundException) {
            return NotFoundHandler.getInstance();
        }

        if (exception instanceof UnauthorizedException) {
            return UnAuthorizationHandler.getInstance();
        }

        return InternalServerErrorHandler.getInstance();
    }
}
