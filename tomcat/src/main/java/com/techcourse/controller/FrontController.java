package com.techcourse.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.HttpStatusLine;
import org.apache.coyote.http.HttpVersion;

public class FrontController implements Controller {

    private static FrontController instance;

    private final Map<String, Controller> controllers;

    private FrontController() {
        controllers = new ConcurrentHashMap<>();
        controllers.put("/", new RootController());
        controllers.put("/login", new LoginController());
        controllers.put("/register", new RegisterController());
        controllers.put("static", new StaticResourceController());
    }

    public static FrontController getInstance() {
        if (instance == null) {
            instance = new FrontController();
        }
        return instance;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) {
        if (request == null) {
            return notFound();
        }
        String path = request.getPath();
        Controller controller;
        if (controllers.containsKey(path)) {
            controller = controllers.get(path);
            return controller.handle(request);
        }
        if (StaticResourceHandler.isStaticResource(request)) {
            controller = controllers.get("static");
            return controller.handle(request);
        }
        return notFound();
    }

    private HttpResponse notFound() {
        return HttpResponse.builder()
                .statusLine(new HttpStatusLine(HttpVersion.HTTP11, HttpStatusCode.NOT_FOUND))
                .build();
    }
}
