package org.apache.catalina.startup;

import java.io.IOException;
import java.util.Set;
import nextstep.jwp.controller.HelloController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.LoginPageController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.RegisterPageController;
import nextstep.jwp.controller.ResourceController;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.adaptor.ControllerAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);
    private static final int DEFAULT_MAX_THREADS = 200;
    private final ControllerAdaptor controllerAdaptor = new ControllerAdaptor(Set.of(
            new HelloController(), new ResourceController(), new LoginPageController(), new LoginController(),
            new RegisterPageController(), new RegisterController())
    );

    public void start() {
        var connector = new Connector(controllerAdaptor, DEFAULT_MAX_THREADS);
        connector.start();

        try {
            // make the application wait until we press any key.
            System.in.read();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("web server stop.");
            connector.stop();
        }
    }
}
