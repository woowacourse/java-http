package org.apache.coyote.http11.config;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.coyote.http11.controller.LoginAbstractController;
import org.apache.coyote.http11.controller.RegisterAbstractController;
import org.apache.coyote.http11.controller.RootAbstractController;
import org.apache.coyote.http11.filter.FilterChainFactory;
import org.apache.coyote.http11.handle.RequestDispatcher;
import org.apache.coyote.http11.handle.RequestHandler;
import org.apache.coyote.http11.handle.StaticResourceHandler;
import org.apache.coyote.http11.handle.ViewResolver;

public class TomcatServerConfiguration {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final String DEFAULT_CHARSET_NAME = DEFAULT_CHARSET.name().toLowerCase();

    public static final String STATIC_RESOURCE_PATH = "static";
    public static final List<RequestHandler> REQUEST_HANDLERS = List.of(
            StaticResourceHandler.create(STATIC_RESOURCE_PATH, DEFAULT_CHARSET),
            RequestDispatcher.create(
                new FilterChainFactory(),
                new ViewResolver(STATIC_RESOURCE_PATH, DEFAULT_CHARSET),
                new RootAbstractController(),
                new LoginAbstractController(),
                new RegisterAbstractController()
            )
    );


}
