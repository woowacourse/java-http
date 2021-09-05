package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.requestline.RequestPath;
import nextstep.jwp.http.response.ContentType;

public class RequestMapping {

    private static final StaticResourceController STATIC_RESOURCE_CONTROLLER = new StaticResourceController();
    private static final NotFoundController NOT_FOUND_CONTROLLER = new NotFoundController();

    private final Map<RequestPath, Controller> controllersMap;

    public RequestMapping() {
        this.controllersMap = Map.of(
            new RequestPath("/login"), new LoginController(),
            new RequestPath("/register"), new RegisterController(),
            new RequestPath("/"), new DefaultController()
        );
    }

    public Controller findController(HttpRequest httpRequest) {
        return controllersMap.getOrDefault(httpRequest.getRequestLine().getRequestURI(),
            findResourceController(httpRequest));
    }

    private Controller findResourceController(HttpRequest httpRequest) {
        if (!httpRequest.getHeader().acceptHtmlType(ContentType.HTML.getValue()) ||
            httpRequest.getRequestLine().getRequestURI().containsExtension(".html")) {
            return STATIC_RESOURCE_CONTROLLER;
        }
        return NOT_FOUND_CONTROLLER;
    }
}
