package nextstep;

import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.catalina.startup.Tomcat.TomcatBuilder;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new TomcatBuilder()
                .addController("/login", new LoginController())
                .addController("/register", new RegisterController())
                .build();
        tomcat.start();
    }
}
