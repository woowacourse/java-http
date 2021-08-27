package nextstep.jwp.server;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.StaticResourceController;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequestParser;
import nextstep.jwp.http.request.RequestHandler;
import nextstep.jwp.http.request.RequestMapping;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.RegisterService;
import nextstep.jwp.staticresource.StaticResourceFinder;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DependencyInjector {

    public RequestHandler getNewRequestHandlerWithConnection(Socket connection) {
        final RequestMapping requestMapping = createAndInjectObjects();
        final HttpRequestParser httpRequestParser = new HttpRequestParser();
        return new RequestHandler(connection, httpRequestParser, requestMapping);
    }

    private RequestMapping createAndInjectObjects() {
        final InMemoryUserRepository inMemoryUserRepository = new InMemoryUserRepository();

        final RegisterService registerService = new RegisterService(inMemoryUserRepository);
        final LoginService loginService = new LoginService(inMemoryUserRepository);

        final StaticResourceFinder staticResourceFinder = new StaticResourceFinder();

        final RegisterController registerController = new RegisterController(staticResourceFinder, registerService);
        final LoginController loginController = new LoginController(staticResourceFinder, loginService);
        final StaticResourceController staticResourceController = new StaticResourceController(staticResourceFinder);

        final Map<String, Controller> controllers = getMappedControllers(registerController, loginController);

        return new RequestMapping(controllers, staticResourceController);
    }

    private Map<String, Controller> getMappedControllers(RegisterController registerController, LoginController loginController) {
        final Map<String, Controller> controllers = new ConcurrentHashMap<>();
        controllers.put("/register", registerController);
        controllers.put("/login", loginController);
        return controllers;
    }
}
