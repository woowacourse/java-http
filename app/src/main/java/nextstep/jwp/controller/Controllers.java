package nextstep.jwp.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.RegisterService;
import nextstep.jwp.service.StaticResourceService;

public class Controllers {

    private static final int ROOT_PATH_INDEX = 1;

    private final Map<String, Controller> restControllers;
    private final Controller staticResourceController;

    private Controllers(Map<String, Controller> restControllers,
                        Controller staticResourceController) {
        this.restControllers = restControllers;
        this.staticResourceController = staticResourceController;
    }

    public static Controllers loadContext() {
        InMemoryUserRepository userRepository = InMemoryUserRepository.initialize();
        LoginService loginService = new LoginService(userRepository);
        RegisterService registerService = new RegisterService(userRepository);
        StaticResourceService staticResourceService = new StaticResourceService();

        Controller staticResourceController = new StaticResourceController(staticResourceService);

        Map<String, Controller> restControllers = new HashMap<>();
        Controller loginController = new LoginController(loginService, staticResourceService);
        restControllers.put("login", loginController);
        Controller registerController = new RegisterController(registerService,
            staticResourceService);
        restControllers.put("register", registerController);

        return new Controllers(restControllers, staticResourceController);
    }

    public HttpResponse doService(HttpRequest httpRequest) throws IOException {
        String requestUri = httpRequest.getUri();
        String rootUri = requestUri.split("/")[ROOT_PATH_INDEX];

        Controller controller = findByUri(rootUri);

        return controller.doService(httpRequest);
    }

    private Controller findByUri(String rootUri) {
        if (restControllers.containsKey(rootUri)) {
            return restControllers.get(rootUri);
        }

        return staticResourceController;
    }
}
