package com.techcourse.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.InvalidResourceException;

public class FrontController implements Controller {
    private static final FrontController instance = new FrontController();
    private static final Logger log = LoggerFactory.getLogger(FrontController.class);

    private final Map<String, Controller> handlerMappings = new HashMap<>();

    public static FrontController getInstance() {
        return instance;
    }

    private FrontController() {
        initHandlerMappings();
    }

    private void initHandlerMappings() {
        handlerMappings.put("/login", LoginController.getInstance());
        handlerMappings.put("/register", RegisterController.getInstance());
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String uri = request.getURI();
        Controller handler = getHandler(request.getURI());

        try {
            handler.service(request, response);
        } catch (InvalidResourceException e) {
            logError(uri, e);

            handleNotFound(request, response);
        } catch (Exception e) {
            logError(uri, e);

            handleInternalServerError(request, response);
        }
    }

    private Controller getHandler(String uri) {
        if (Objects.nonNull(handlerMappings.get(uri))) {
            return handlerMappings.get(uri);
        }
        return StaticResourceController.getInstance();
    }

    private void handleNotFound(HttpRequest request, HttpResponse response) throws Exception {
        Controller notFoundController = NotFoundController.getInstance();
        notFoundController.service(request, response);
    }

    private void handleInternalServerError(HttpRequest request, HttpResponse response) throws Exception {
        Controller internalServerErrorController = InternalServerErrorController.getInstance();
        internalServerErrorController.service(request, response);
    }

    private void logError(String uri, Exception e) {
        log.error("Error processing request for endpoint: {}, message: {}", uri, e.getMessage());
    }
}
