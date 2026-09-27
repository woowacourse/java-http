package com.techcourse;

import com.techcourse.controller.LoginAbstractController;
import com.techcourse.controller.RegisterAbstractController;
import com.techcourse.controller.RootAbstractController;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.CoyoteAdapter;
import org.apache.catalina.filter.FilterChainFactory;
import org.apache.catalina.handle.RequestDispatcher;
import org.apache.catalina.handle.RequestHandler;
import org.apache.catalina.handle.RequestHandlerResolver;
import org.apache.catalina.handle.StaticResourceHandler;
import org.apache.catalina.handle.ViewResolver;
import org.apache.catalina.startup.Tomcat;

public class Application {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final String  DEFAULT_CHARSET_NAME = DEFAULT_CHARSET.name().toLowerCase();
    public static final String  STATIC_RESOURCE_PATH = "static";

    public static void main(String[] args) {
        final var edenCoyoteAdapter = new CoyoteAdapter(createRequestHandlerResolver());
        final var connector = new Connector(edenCoyoteAdapter);
        final var tomcat = new Tomcat(connector);

        tomcat.start();
    }

    public static RequestHandlerResolver createRequestHandlerResolver() {
        final RequestHandlerResolver requestHandlerResolver = new RequestHandlerResolver();
        final RequestHandler resourceHandler = StaticResourceHandler.create(STATIC_RESOURCE_PATH, DEFAULT_CHARSET);

        final RequestHandler requestDispatcher = RequestDispatcher.create(
                new FilterChainFactory(),
                new ViewResolver(STATIC_RESOURCE_PATH, DEFAULT_CHARSET),
                new RootAbstractController(),
                new LoginAbstractController(),
                new RegisterAbstractController()
        );

        requestHandlerResolver.registerLast(resourceHandler);
        requestHandlerResolver.registerLast(requestDispatcher);

        return requestHandlerResolver;
    }
}
