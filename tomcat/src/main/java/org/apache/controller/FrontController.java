package org.apache.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.controller.path.HelloController;
import org.apache.controller.path.LoginController;
import org.apache.controller.path.PathController;
import org.apache.controller.path.RegisterController;
import org.apache.request.HttpRequest;
import org.apache.request.RequestLine;
import org.apache.request.RequestUri;
import org.apache.response.HttpResponse;
import org.apache.util.StaticResourceFinder;

public class FrontController implements Controller {

    private static FrontController instance = new FrontController();

    private Map<String, PathController> pathControllers;

    private FrontController() {
        this.pathControllers = new HashMap<>();
        enrollPathController(HelloController.getInstance());
        enrollPathController(LoginController.getInstance());
        enrollPathController(RegisterController.getInstance());
    }

    private void enrollPathController(PathController pathController) {
        pathControllers.put(pathController.getPath(), pathController);
    }

    public static FrontController getInstance() {
        return instance;
    }

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        RequestLine requestLine = httpRequest.getRequestLine();

        if (isPathControllerEnrolled(requestLine)) {
            Controller handler = pathControllers.get(requestLine.getRequestUri().parsePath());
            handler.service(httpRequest, httpResponse);
            return;
        }

        if (StaticResourceFinder.isStaticResourceExist(requestLine.getRequestUri())) {
            httpResponse.setStaticResource(requestLine.getRequestUri());
        } else {
            httpResponse.setStaticResource(new RequestUri("/404.html"));
        }
    }

    private boolean isPathControllerEnrolled(final RequestLine requestLine) {
        return pathControllers.containsKey(requestLine.getRequestUri().parsePath());
    }
}
