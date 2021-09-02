package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.requestline.RequestURI;
import nextstep.jwp.http.response.ContentType;

public class Controllers {

    private static final StaticResourceController STATIC_RESOURCE_CONTROLLER = new StaticResourceController();
    private static final NotFoundController NOT_FOUND_CONTROLLER = new NotFoundController();
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
            findResourceController(httpRequest));
    }

    private Controller findResourceController(HttpRequest httpRequest) {
        if (!httpRequest.getHeader().contains(ContentType.HTML.getValue()) ||
            httpRequest.getRequestLine().getRequestURI().containsExtension(".html")) {
            return STATIC_RESOURCE_CONTROLLER;
        }
        return NOT_FOUND_CONTROLLER;
    }
}
