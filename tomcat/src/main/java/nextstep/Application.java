package nextstep;

import nextstep.jwp.HandlerMapping;
import nextstep.jwp.JwpHttpDispatcher;
import nextstep.jwp.SessionManager;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.get.LoginGetHandler;
import nextstep.jwp.handler.get.RegisterGetHandler;
import nextstep.jwp.handler.get.RootGetHandler;
import nextstep.jwp.handler.post.LoginPostHandler;
import nextstep.jwp.handler.post.RegisterPostHandler;
import nextstep.jwp.interceptor.AuthInterceptor;
import nextstep.jwp.interceptor.HandlerInterceptor;
import org.apache.catalina.startup.Tomcat;
import java.util.List;
import java.util.Map;

public class Application {

    private static final Map<String, Handler> httpGetHandlers =
            Map.of("/", new RootGetHandler(),
                    "/login", new LoginGetHandler(new SessionManager()),
                    "/register", new RegisterGetHandler());
    private static final Map<String, Handler> httpPostHandlers =
            Map.of("/login", new LoginPostHandler(new SessionManager()),
                    "/register", new RegisterPostHandler(new SessionManager()));

    private static final List<HandlerInterceptor> handlerInterceptors =
            List.of(new AuthInterceptor(List.of("/login"), new SessionManager()));

    public static void main(final String[] args) {
        final var tomcat = new Tomcat(new JwpHttpDispatcher(new HandlerMapping(httpGetHandlers, httpPostHandlers, handlerInterceptors)));
        tomcat.start();
    }
}
