package nextstep;

import nextstep.jwp.controller.FrontController;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(String[] args) {
        final var controller = new FrontController();
        final var tomcat = new Tomcat(controller);
        tomcat.start();
    }

}
