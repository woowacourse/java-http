package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.requestline.RequestURI;

public class Controllers {

    private static final StaticResourceController STATIC_RESOURCE_CONTROLLER = new StaticResourceController();
    private final Map<RequestURI, Controller> controllersMap;

    public Controllers() {
        this.controllersMap = Map.of(
            new RequestURI("/login"), new LoginController(),
            new RequestURI("/register"), new RegisterController(),
            new RequestURI("/index"), new IndexController(),
            new RequestURI("/"), new DefaultController()
        );
    }

    public Controller findController(HttpRequest httpRequest) {
        return controllersMap.getOrDefault(httpRequest.getRequestLine().getRequestURI(),
            STATIC_RESOURCE_CONTROLLER);
    }
}
