package nextstep;

import nextstep.jwp.ui.AuthController;
import nextstep.jwp.ui.HomeController;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("web server start.");
        new AuthController().init();
        new HomeController().init();
        final var tomcat = new Tomcat();
        tomcat.start();
    }
}
