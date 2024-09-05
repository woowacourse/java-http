package com.techcourse.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.coyote.http11.Http11Helper;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UnsupportedMethodException;

public class FrontController extends Controller {
    private static final Logger log = LoggerFactory.getLogger(FrontController.class);

    private final Http11Helper http11Helper = Http11Helper.getInstance();
    private final Map<String, Controller> handlerMappings = new HashMap<>();

    public FrontController() {
        initHandlerMappings();
    }

    private void initHandlerMappings() {
        handlerMappings.put("/", new ViewController());
        handlerMappings.put("/hello", new ViewController());
        handlerMappings.put("/index", new ViewController());
        handlerMappings.put("/login", new LoginController());
        handlerMappings.put("/register", new RegisterController());
    }

    @Override
    public String handle(HttpRequest request) throws IOException {
        Controller handler = getHandler(request);
        if (Objects.isNull(handler)) {
            log.error("Error processing request for endpoint: {}", request.getURI());

            String response = http11Helper.createResponse(HttpStatus.NOT_FOUND, "404.html");
            return response;
        }
        try {
            String response = handler.handle(request);

            return response;
        } catch (UnsupportedMethodException e) {
            log.error("Error processing request for endpoint: {}", request.getURI());

            String response = http11Helper.createResponse(HttpStatus.METHOD_NOT_ALLOWED, "405.html");
            return response;
        }
    }

    private Controller getHandler(HttpRequest request) {
        String requestURI = request.getURI();
        return handlerMappings.get(requestURI);
    }

    @Override
    protected String doPost(HttpRequest request) throws IOException {
        throw new UnsupportedMethodException("Method is not supported");
    }

    @Override
    protected String doGet(HttpRequest request) throws IOException {
        throw new UnsupportedMethodException("Method is not supported");
    }
}
