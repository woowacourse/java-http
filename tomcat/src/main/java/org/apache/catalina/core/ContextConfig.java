package org.apache.catalina.core;

import java.util.List;
import org.apache.catalina.RequestHandler;
import org.apache.catalina.controller.ApplicationContextConfig;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.exception.ExceptionHandler;
import org.apache.catalina.handler.ControllerHandler;
import org.apache.catalina.handler.StaticResourceRequestHandler;
import org.apache.catalina.resolver.ViewResolver;
import org.apache.catalina.resource.ResourceLoader;
import org.apache.catalina.resource.StaticResourceLoader;
import org.apache.catalina.resource.ViewResourceLoader;

public class ContextConfig {

    public final static RequestMapping HANDLER_MAPPING = ApplicationContextConfig.REQUEST_MAPPING;

    public final static ResourceLoader STATIC_RESOURCE_MAPPER = new StaticResourceLoader();
    public final static ViewResolver VIEW_RESOLVER = new ViewResolver(new ViewResourceLoader());
    public final static ExceptionHandler EXCEPTION_HANDLER = new ExceptionHandler(VIEW_RESOLVER);

    public final static List<RequestHandler> REQUEST_HANDLERS = List.of(
            new StaticResourceRequestHandler(STATIC_RESOURCE_MAPPER),
            new ControllerHandler(HANDLER_MAPPING, VIEW_RESOLVER)
    );
}
