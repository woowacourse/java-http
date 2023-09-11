package nextstep;

import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.catalina.startup.Tomcat;

import static nextstep.jwp.controller.URIPath.LOGIN_URI;
import static nextstep.jwp.controller.URIPath.REGISTER_URI;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat()
                .addController(LOGIN_URI, new LoginController())
                .addController(REGISTER_URI, new RegisterController());
        tomcat.start();
    }
}
