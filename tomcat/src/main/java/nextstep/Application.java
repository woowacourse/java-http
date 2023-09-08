package nextstep;

import nextstep.jwp.controller.FrontHandler;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static void main(final String[] args) {
        final var tomcat = new Tomcat(new FrontHandler());
        tomcat.start();
    }
}
