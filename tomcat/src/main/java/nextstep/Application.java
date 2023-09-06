package nextstep;

import org.apache.coyote.handler.WelcomeHandler;
import nextstep.jwp.application.UserService;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.Container;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();
        final Container container = tomcat.addContainer("/");

        container.addHandler(new WelcomeHandler());

        tomcat.start();
    }

    private static UserService userService() {
        return new UserService();
    }
}
