package nextstep;

import nextstep.jwp.HandlerMapping;
import nextstep.jwp.JwpHttpDispatcher;
import nextstep.jwp.SessionManager;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.RootGetController;
import nextstep.jwp.interceptor.AuthInterceptor;
import nextstep.jwp.interceptor.HandlerInterceptor;
import org.apache.catalina.startup.Tomcat;
import java.util.List;
import java.util.Map;

public class Application {

    private static final Map<String, Controller> controllers =
            Map.of("/", new RootGetController(),
                    "/login", new LoginController(new SessionManager()),
                    "/register", new RegisterController(new SessionManager()));
    private static final List<HandlerInterceptor> handlerInterceptors =
            List.of(new AuthInterceptor(List.of("/login"), new SessionManager()));

    public static void main(final String[] args) {
        final var tomcat = new Tomcat(new JwpHttpDispatcher(new HandlerMapping(controllers, handlerInterceptors)));
        tomcat.start();
    }
}
