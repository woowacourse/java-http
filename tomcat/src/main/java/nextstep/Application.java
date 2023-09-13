package nextstep;

import java.util.Map;
import nextstep.jwp.handler.DispatcherController;
import nextstep.jwp.handler.LoginHandler;
import nextstep.jwp.handler.RootPageRequestHandler;
import nextstep.jwp.handler.SignUpRequestHandler;
import nextstep.jwp.handler.StaticResourceRequestHandler;
import nextstep.jwp.handler.mappaing.HandlerMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        HandlerMapping mapping = new HandlerMapping(Map.of(
                "/", new RootPageRequestHandler(),
                "/login", new LoginHandler(),
                "/register", new SignUpRequestHandler(),
                "/**", new StaticResourceRequestHandler()
        ));
        DispatcherController dispatcherServlet = new DispatcherController(mapping);
        final var tomcat = new Tomcat(dispatcherServlet);
        tomcat.start();
    }
}
