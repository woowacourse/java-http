package com.techcourse.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UnsupportedMethodException;

public class FrontController extends Controller {
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
        handlerMappings.put("/", ViewController.getInstance());
        handlerMappings.put("/hello", ViewController.getInstance());
        handlerMappings.put("/index", ViewController.getInstance());
        handlerMappings.put("/login", LoginController.getInstance());
        handlerMappings.put("/register", RegisterController.getInstance());
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        Controller handler = getHandler(request.getURI());
        if (Objects.isNull(handler)) {
            log.error("Error processing request for endpoint: {}", request.getURI());

            handler = NotFoundController.getInstance();
        }
        try {
            HttpResponse response = handler.handle(request);
            return response;
        } catch (UnsupportedMethodException e) {
            log.error("Error processing request for endpoint: {}", request.getURI());

            handler = MethodNotAllowedController.getInstance();
            HttpResponse response = handler.handle(request);
            return response;
        }
    }

    private Controller getHandler(String uri) {
        return handlerMappings.get(uri);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws IOException {
        throw new UnsupportedMethodException("Method is not supported");
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        throw new UnsupportedMethodException("Method is not supported");
    }
}
