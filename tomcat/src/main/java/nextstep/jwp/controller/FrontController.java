package nextstep.jwp.controller;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.controller.path.HelloController;
import nextstep.jwp.controller.path.LoginController;
import nextstep.jwp.controller.path.PathController;
import nextstep.jwp.controller.path.RegisterController;
import web.request.HttpRequest;
import web.request.RequestLine;
import web.request.RequestUri;
import web.response.HttpResponse;
import web.util.StaticResourceFinder;

public class FrontController implements Controller {

    private static FrontController instance;

    private final Map<String, PathController> pathControllers;

    private FrontController() {
        this.pathControllers = new HashMap<>();
        enrollPathController(HelloController.getInstance());
        enrollPathController(LoginController.getInstance());
        enrollPathController(RegisterController.getInstance());
    }

    private void enrollPathController(final PathController pathController) {
        pathControllers.put(pathController.getPath(), pathController);
    }

    public static FrontController getInstance() {
        if (instance == null) {
            instance = new FrontController();
        }
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
