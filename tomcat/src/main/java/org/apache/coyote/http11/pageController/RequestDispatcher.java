package org.apache.coyote.http11.pageController;

import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.BadRequestException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.InternalServerErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestDispatcher {
    private static final Logger log = LoggerFactory.getLogger(RequestDispatcher.class);

    private final Map<String, PageController> controllers;
    private final PageController defaultController;
    private final InternalServerErrorHandler internalServerErrorHandler = new InternalServerErrorHandler();

    public RequestDispatcher() {
        this(
            Map.of("/login", new LoginController(), "/register", new RegisterController()),
            new StaticResourceController()
        );
    }

    public RequestDispatcher(Map<String, PageController> controllers, PageController defaultController) {
        this.controllers = Map.copyOf(controllers);
        this.defaultController = defaultController;
    }

    public void dispatch(HttpRequest request, HttpResponse response) {
        PageController controller = getPageController(request.getHttpPath());

        try {
            controller.service(request, response);
        } catch (BadRequestException e) {
            throw e;
        } catch (IOException | RuntimeException e) {
            log.error(e.getMessage(), e);
            internalServerErrorHandler.handle(response);
        }
    }

    public PageController getPageController(String path) {
        return controllers.getOrDefault(path, defaultController);
    }
}
