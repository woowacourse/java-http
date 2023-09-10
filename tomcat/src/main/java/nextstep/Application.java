package nextstep;

import java.util.Set;
import nextstep.jwp.handler.LoginRequestHandler;
import nextstep.jwp.handler.RegisterRequestHandler;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(final String[] args) {
        final var tomcat = new Tomcat();
        tomcat.start(Set.of(new LoginRequestHandler(), new RegisterRequestHandler()));
    }
}
