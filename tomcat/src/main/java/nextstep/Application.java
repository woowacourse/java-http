package nextstep;

import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.controller.ControllerContainer;
import org.apache.coyote.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        ControllerContainer.scanPackage("nextstep.jwp.controller");

        log.info("web server start.");
        final var tomcat = new Tomcat();
        tomcat.start();
    }
}
