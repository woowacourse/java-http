package org.apache.coyote;

import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.StaticResourceController;
import org.apache.coyote.exception.NotFoundException;
import org.apache.coyote.exception.UnauthorizedException;
import org.apache.http.request.HttpRequest;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootEndPointController;
import com.techcourse.controller.exception.InternalServerErrorController;
import com.techcourse.controller.exception.NotFoundController;
import com.techcourse.controller.exception.UnAuthorizationController;


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
            return RootEndPointController.getInstance();
        }

        if (path.equals("/login")) {
            return LoginController.getInstance();
        }

        if (path.equals("/register")) {
            return RegisterController.getInstance();
        }

        return StaticResourceController.getInstance();
    }

    public Controller getHandlerByException(final Exception exception) {
        if (exception instanceof NotFoundException) {
            return NotFoundController.getInstance();
        }

        if (exception instanceof UnauthorizedException) {
            return UnAuthorizationController.getInstance();
        }

        return InternalServerErrorController.getInstance();
    }
}
