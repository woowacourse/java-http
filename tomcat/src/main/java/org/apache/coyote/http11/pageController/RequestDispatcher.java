package org.apache.coyote.http11.pageController;

import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.BadRequestException;
import org.apache.coyote.http11.StaticResourceLoader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestDispatcher {
    private static final String SERVER_ERROR_PAGE = "/500.html";
    private static final StaticResourceLoader STATIC_RESOURCE_LOADER = new StaticResourceLoader();

    private static final Logger log = LoggerFactory.getLogger(RequestDispatcher.class);

    private final Map<String, PageController> controllers;
    private final PageController defaultController;

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
            internalServerError(response);
        }
    }

    public PageController getPageController(String path) {
        return controllers.getOrDefault(path, defaultController);
    }

    private void internalServerError(HttpResponse response) {
        response.reset();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

        try {
            response.setStaticResource(HttpStatus.INTERNAL_SERVER_ERROR, STATIC_RESOURCE_LOADER.load(SERVER_ERROR_PAGE));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            response.setBody("text/plain", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }
}
