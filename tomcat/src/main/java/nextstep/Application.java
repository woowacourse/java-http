package nextstep;

import java.util.List;
import nextstep.jwp.handler.LoginPageHandler;
import nextstep.jwp.handler.LoginRequestHandler;
import nextstep.jwp.handler.RootPageRequestHandler;
import nextstep.jwp.handler.SignUpRequestHandler;
import nextstep.jwp.handler.StaticResourceRequestHandler;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.servlet.handler.RequestHandler;

public class Application {

    public static void main(String[] args) {
        List<RequestHandler> requestHandlers = List.of(
                new RootPageRequestHandler(),
                new LoginPageHandler(),
                new LoginRequestHandler(),
                new SignUpRequestHandler(),
                new StaticResourceRequestHandler()
        );
        final var tomcat = new Tomcat(requestHandlers);
        tomcat.start();
    }
}
