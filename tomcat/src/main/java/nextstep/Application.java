package nextstep;

import nextstep.jwp.JwpFrontController;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.handler.FrontController;

public class Application {

    private static final FrontController frontController = new JwpFrontController();

    public static void main(final String[] args) {
        final var tomcat = new Tomcat(frontController);
        tomcat.start();
    }
}
