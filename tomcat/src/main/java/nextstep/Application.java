package nextstep;

import nextstep.jwp.controller.handlermapping.JwpExceptionHandlers;
import nextstep.jwp.controller.handlermapping.JwpRequestMapping;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("web server start.");
        final var tomcat = new Tomcat(JwpRequestMapping.getInstance(), JwpExceptionHandlers.getInstance());
        tomcat.start();
    }
}
