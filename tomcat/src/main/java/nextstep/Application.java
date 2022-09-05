package nextstep;

import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.componentscan.ComponentScan;
import org.apache.coyote.requestmapping.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("web server start.");
        init();
        final var tomcat = new Tomcat();
        tomcat.start();
    }

    private static void init() {
        ComponentScan.scan();
        Registry.register();
    }
}
