package org.apache.catalina;

import java.util.List;
import org.apache.catalina.core.CatalinaContainer;
import org.apache.catalina.exception.ExceptionHandler;
import org.apache.catalina.handler.ControllerHandler;
import org.apache.catalina.handler.RequestHandlerMapper;
import org.apache.catalina.handler.StaticResourceRequestHandler;
import org.apache.catalina.resources.ResourceManager;
import org.apache.catalina.resources.StaticResourceManager;
import org.apache.catalina.resources.ViewResourceManager;
import org.apache.catalina.web.controller.ApplicationContextConfig;
import org.apache.catalina.web.resolver.ViewResolver;

public class ContextConfig {

    public final static RequestMapping HANDLER_MAPPING = ApplicationContextConfig.REQUEST_MAPPING;

    public final static ResourceManager STATIC_RESOURCE_MAPPER = new StaticResourceManager();
    public final static ResourceManager VIEW_RESOURCE_LOADER = new ViewResourceManager();
    public final static ViewResolver VIEW_VIEW_RESOLVER = new ViewResolver(VIEW_RESOURCE_LOADER);
    public final static ExceptionHandler EXCEPTION_HANDLER = new ExceptionHandler(VIEW_VIEW_RESOLVER);

    public final static RequestHandlerMapper REQUEST_HANDLER_MAPPER =
            new RequestHandlerMapper(
                    List.of(
                            new StaticResourceRequestHandler(STATIC_RESOURCE_MAPPER),
                            new ControllerHandler(HANDLER_MAPPING, VIEW_VIEW_RESOLVER)
                    )
            );
    public final static CatalinaContainer CATALINA_CONTAINER = new CatalinaContainer(
            ContextConfig.REQUEST_HANDLER_MAPPER,
            ContextConfig.EXCEPTION_HANDLER
    );
}
