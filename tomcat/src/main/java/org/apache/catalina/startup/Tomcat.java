package org.apache.catalina.startup;

import java.util.List;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.handler.Handler;
import org.apache.coyote.http11.handler.DispatcherHandler;
import org.apache.coyote.http11.handler.LoginHandler;
import org.apache.coyote.http11.handler.StaticResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    public void start() {
        List<Handler> handlers = List.of(
                new StaticResourceHandler("static", "index.html"),
                new LoginHandler()
        );

        DispatcherHandler dispatcher = new DispatcherHandler(handlers);

        Connector connector = new Connector(dispatcher);
        connector.start();

        try {
            System.in.read();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("web server stop.");
            connector.stop();
        }
    }
}
