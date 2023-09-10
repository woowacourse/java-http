package nextstep;

import java.util.List;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var tomcat = Tomcat.from(List.of(new LoginController(), new RegisterController()));
        tomcat.start();
    }
}
