package nextstep;

import nextstep.jwp.config.WebConfig;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        new WebConfig().init();
        log.info("web server start.");
        final var tomcat = new Tomcat();
        tomcat.start();
    }
}
