package nextstep;

import nextstep.jwp.controller.GetHomeController;
import nextstep.jwp.controller.GetIndexController;
import nextstep.jwp.controller.GetLoginController;
import nextstep.jwp.controller.PostLoginController;
import nextstep.jwp.controller.PostRegisterController;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(final String[] args) {
        log.info("web server start.");

        final RequestMapping requestMapping = RequestMapping.of(
                new GetIndexController(),
                new GetHomeController(),
                new PostLoginController(),
                new GetLoginController(),
                new PostRegisterController()
        );

        final var tomcat = new Tomcat();
        tomcat.start(requestMapping);
    }
}
