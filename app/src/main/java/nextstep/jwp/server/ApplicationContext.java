package nextstep.jwp.server;

import nextstep.jwp.controller.*;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequestParser;
import nextstep.jwp.http.request.RequestHandler;
import nextstep.jwp.http.request.RequestMapping;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.RegisterService;
import nextstep.jwp.staticresource.StaticResourceFinder;

import java.util.Map;

public class ApplicationContext {

    private final RequestHandler requestHandler;

    public ApplicationContext() {
        final RequestMapping requestMapping = createAndInjectObjects();
        final HttpRequestParser httpRequestParser = new HttpRequestParser();
        requestHandler = new RequestHandler(httpRequestParser, requestMapping);
    }

    private RequestMapping createAndInjectObjects() {
        final InMemoryUserRepository inMemoryUserRepository = new InMemoryUserRepository();

        final RegisterService registerService = new RegisterService(inMemoryUserRepository);
        final LoginService loginService = new LoginService(inMemoryUserRepository);

        final StaticResourceFinder staticResourceFinder = new StaticResourceFinder();
        final StaticResourceController staticResourceController = new StaticResourceController(staticResourceFinder);

        final Map<String, Controller> controllers = Map.of(
                "/", new HomeController(staticResourceFinder),
                "/register", new RegisterController(staticResourceFinder, registerService),
                "/login", new LoginController(staticResourceFinder, loginService));

        return new RequestMapping(controllers, staticResourceController);
    }
    public RequestHandler getRequestHandler() {
        return requestHandler;
    }
}
