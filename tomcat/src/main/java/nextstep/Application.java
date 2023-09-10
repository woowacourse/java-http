package nextstep;

import java.util.Set;
import nextstep.jwp.servlet.LoginRequestServlet;
import nextstep.jwp.servlet.RegisterRequestServlet;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(final String[] args) {
        final var tomcat = new Tomcat();
        tomcat.start(Set.of(new LoginRequestServlet(), new RegisterRequestServlet()));
    }
}
