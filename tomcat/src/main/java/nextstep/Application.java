package nextstep;

import nextstep.jwp.HandlerResolver;
import nextstep.jwp.JwpHttpDispatcher;
import nextstep.jwp.SessionManager;
import nextstep.jwp.handler.get.LoginGetController;
import nextstep.jwp.handler.get.RegisterGetController;
import nextstep.jwp.handler.get.RootGetController;
import nextstep.jwp.handler.post.LoginPostController;
import nextstep.jwp.handler.post.RegisterPostController;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Controller;
import java.util.Map;

public class Application {

    private static final Map<String, Controller> httpGetHandlers =
            Map.of("/", new RootGetController(),
                    "/login", new LoginGetController(new SessionManager()),
                    "/register", new RegisterGetController());
    private static final Map<String, Controller> httpPostHandlers =
            Map.of("/login", new LoginPostController(new SessionManager()),
                    "/register", new RegisterPostController());

    public static void main(final String[] args) {
        final var tomcat = new Tomcat(new JwpHttpDispatcher(new HandlerResolver(httpGetHandlers, httpPostHandlers)));
        tomcat.start();
    }
}
