package nextstep;

import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.IndexController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(final String[] args) {
        log.info("web server start.");

        final RequestMapping requestMapping = RequestMapping.builder()
                .add("/", HomeController.getInstance())
                .add("/index.html", IndexController.getInstance())
                .add("/login", LoginController.getInstance())
                .add("/register", RegisterController.getInstance())
                .addResourceController(ResourceController.getInstance())
                .build();

        final var tomcat = new Tomcat();
        tomcat.start(requestMapping);
    }
}
