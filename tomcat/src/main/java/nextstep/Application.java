package nextstep;

import org.apache.coyote.handler.WelcomeHandler;
import nextstep.jwp.application.UserService;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.Context;

public class Application {

    public static void main(String[] args) {
        final var tomcat = new Tomcat();
        final Context context = tomcat.addContainer("/");

        context.addHandler(new WelcomeHandler());

        tomcat.start();
    }

    private static UserService userService() {
        return new UserService();
    }
}
