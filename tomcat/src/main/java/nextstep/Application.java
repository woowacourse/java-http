package nextstep;

import java.util.Set;
import nextstep.jwp.handler.LoginRequestHandler;
import nextstep.jwp.handler.RegisterRequestHandler;
import nextstep.jwp.handler.StaticResourceRequestHandler;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(final String[] args) {
        final var tomcat = new Tomcat();
        /// TODO: 2023/09/10 StaticResourceRequestHandler는 톰캣에서 직접 관리해야 하지 않을까?
        tomcat.start(
                Set.of(new StaticResourceRequestHandler(), new LoginRequestHandler(), new RegisterRequestHandler())
        );
    }
}
