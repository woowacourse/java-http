package nextstep;

import nextstep.jwp.ui.HomeController;
import nextstep.jwp.ui.controller.Controllers;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("web server start.");
        final var controllers = new Controllers();
        final var tomcat = new Tomcat(new HomeController(controllers));
        tomcat.start();
    }
}
