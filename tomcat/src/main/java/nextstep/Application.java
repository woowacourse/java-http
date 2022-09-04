package nextstep;

import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.controller.UserController;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("web server start.");
        final var userController = new UserController();
        final var tomcat = new Tomcat();
        tomcat.start();
    }
}
