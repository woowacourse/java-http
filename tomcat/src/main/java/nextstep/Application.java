package nextstep;

import nextstep.jwp.config.JwpRequestMapping;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(final String[] args) {
        final RequestMapping requestMapping = new JwpRequestMapping();

        log.info("web server start.");
        final var tomcat = new Tomcat();
        tomcat.start(requestMapping);
    }
}
