package nextstep;

import nextstep.jwp.support.JwpRequestMapping;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(final String[] args) {
        log.info("web server start.");
        final var tomcat = new Tomcat(new JwpRequestMapping());
        tomcat.start();
    }
}
