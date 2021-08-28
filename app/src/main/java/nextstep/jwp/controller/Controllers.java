package nextstep.jwp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.service.StaticResourceService;

public class Controllers {

    private final List<Controller> restControllers;
    private final Controller staticResourceController;

    private Controllers(List<Controller> restControllers, Controller staticResourceController) {
        this.restControllers = restControllers;
        this.staticResourceController = staticResourceController;
    }

    public static Controllers loadContext() {
        StaticResourceService staticResourceService = new StaticResourceService();

        Controller staticResourceController = new StaticResourceController(staticResourceService);

        Controller loginController = new LoginController(staticResourceService);

        List<Controller> controllers = Arrays.asList(loginController);

        return new Controllers(controllers, staticResourceController);
    }

    public HttpResponse doService(HttpRequest httpRequest) throws IOException {
        Controller controller = findByUri(httpRequest.getUri());

        return controller.doService(httpRequest);
    }

    private Controller findByUri(String requestUri) {
        return restControllers.stream()
            .filter(controller -> controller.matchUri(requestUri))
            .findAny()
            .orElse(staticResourceController);
    }
}
