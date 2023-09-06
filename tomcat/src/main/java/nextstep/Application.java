package nextstep;

import nextstep.jwp.HandlerResolver;
import nextstep.jwp.JwpHttpDispatcher;
import nextstep.jwp.SessionManager;
import nextstep.jwp.handler.get.LoginGetHandler;
import nextstep.jwp.handler.get.RegisterGetHandler;
import nextstep.jwp.handler.get.RootGetHandler;
import nextstep.jwp.handler.post.LoginPostHandler;
import nextstep.jwp.handler.post.RegisterPostHandler;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Handler;
import java.util.Map;

public class Application {

    private static final Map<String, Handler> httpGetHandlers =
            Map.of("/", new RootGetHandler(),
                    "/login", new LoginGetHandler(new SessionManager()),
                    "/register", new RegisterGetHandler());
    private static final Map<String, Handler> httpPostHandlers =
            Map.of("/login", new LoginPostHandler(new SessionManager()),
                    "/register", new RegisterPostHandler());

    public static void main(final String[] args) {
        final var tomcat = new Tomcat(new JwpHttpDispatcher(new HandlerResolver(httpGetHandlers, httpPostHandlers)));
        tomcat.start();
    }
}
