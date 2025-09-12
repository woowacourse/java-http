package org.apache.catalina.startup;

import java.io.IOException;
import java.util.List;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.controller.LoginController;
import org.apache.catalina.controller.RegisterController;
import org.apache.catalina.handler.DispatcherHandler;
import org.apache.catalina.handler.HandlerMapping;
import org.apache.catalina.handler.RequestMappingHandlerMapping;
import org.apache.catalina.handler.statics.StaticResourceHandlerMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    public void start() {
        List<HandlerMapping> handlerMappings = List.of(
                new StaticResourceHandlerMapping(),
                createRequestMappingHandlerMapping()
        );
        DispatcherHandler dispatcher = new DispatcherHandler(handlerMappings);
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

    private RequestMappingHandlerMapping createRequestMappingHandlerMapping () {
        RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();
        mapping.addMapping("/login", new LoginController());
        mapping.addMapping("/register", new RegisterController());
        return mapping;
    }
}
