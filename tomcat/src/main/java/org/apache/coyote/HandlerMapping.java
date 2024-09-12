package org.apache.coyote;

import org.apache.coyote.exception.NotFoundException;
import org.apache.coyote.exception.UnauthorizedException;
import org.apache.coyote.handler.Controller;
import org.apache.coyote.handler.StaticResourceHandler;
import org.apache.http.request.HttpRequest;

import com.techcourse.controller.LoginHandler;
import com.techcourse.controller.RegisterHandler;
import com.techcourse.controller.RootEndPointHandler;
import com.techcourse.controller.exception.InternalServerErrorHandler;
import com.techcourse.controller.exception.NotFoundHandler;
import com.techcourse.controller.exception.UnAuthorizationHandler;


public class HandlerMapping {

    private static final HandlerMapping INSTANCE = new HandlerMapping();
    private static final String PATH_DELIMITER = "/";

    private HandlerMapping() {
    }

    public static HandlerMapping getInstance() {
        return INSTANCE;
    }

    public Controller getHandler(final HttpRequest httpRequest) {
        return getHandlerByEndPoint(httpRequest);
    }

    private Controller getHandlerByEndPoint(final HttpRequest httpRequest) {
        final String path = httpRequest.getPath();

        if (path.equals(PATH_DELIMITER)) {
            return RootEndPointHandler.getInstance();
        }

        if (path.equals("/login")) {
            return LoginHandler.getInstance();
        }

        if (path.equals("/register")) {
            return RegisterHandler.getInstance();
        }

        return StaticResourceHandler.getInstance();
    }

    public Controller getHandlerByException(final Exception exception) {
        if (exception instanceof NotFoundException) {
            return NotFoundHandler.getInstance();
        }

        if (exception instanceof UnauthorizedException) {
            return UnAuthorizationHandler.getInstance();
        }

        return InternalServerErrorHandler.getInstance();
    }
}
