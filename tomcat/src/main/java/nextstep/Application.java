package nextstep;

import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.RootController;
import org.apache.coyote.http11.handler.HandlerStatus;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();
        tomcat.addController(new HandlerStatus("/"), new RootController());
        tomcat.addController(new HandlerStatus("/login"), new LoginController());
        tomcat.addController(new HandlerStatus("/register"), new RegisterController());
        tomcat.start();
    }
}
