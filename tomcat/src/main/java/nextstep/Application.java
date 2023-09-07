package nextstep;

import java.util.List;
import nextstep.jwp.handler.DispatcherServlet;
import nextstep.jwp.handler.LoginPageHandler;
import nextstep.jwp.handler.LoginRequestHandler;
import nextstep.jwp.handler.RootPageRequestHandler;
import nextstep.jwp.handler.SignUpRequestHandler;
import nextstep.jwp.handler.StaticResourceRequestHandler;
import nextstep.jwp.handler.mappaing.HandlerMapping;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        HandlerMapping mapping = new HandlerMapping(List.of(
                new RootPageRequestHandler(),
                new LoginPageHandler(),
                new LoginRequestHandler(),
                new SignUpRequestHandler(),
                new StaticResourceRequestHandler()
        ));
        DispatcherServlet dispatcherServlet = new DispatcherServlet(mapping);
        final var tomcat = new Tomcat(dispatcherServlet);
        tomcat.start();
    }
}
