package nextstep;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.EdenConfig;
import org.apache.coyote.http11.RequestMapping;
import org.apache.coyote.http11.Controller;
import nextstep.jwp.ui.HomeController;
import nextstep.jwp.ui.LoginController;
import nextstep.jwp.ui.RegisterController;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("web server start.");
        final var tomcat = new Tomcat(new EdenConfig());
        tomcat.start();
    }
}
