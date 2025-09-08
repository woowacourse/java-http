package org.apache.catalina.context;

import com.techcourse.controller.DefaultController;
import com.techcourse.controller.UserController;
import java.util.List;
import java.util.Map;
import org.apache.catalina.Controller;
import org.apache.catalina.exception.ExceptionHandler;
import org.apache.catalina.handler.ControllerHandler;
import org.apache.catalina.handler.HandlerMapping;
import org.apache.catalina.handler.RequestHandler;
import org.apache.catalina.handler.StaticResourceRequestHandler;
import org.apache.catalina.resolver.ViewResolver;
import org.apache.catalina.resource.ResourceLoader;
import org.apache.catalina.resource.StaticResourceLoader;
import org.apache.catalina.resource.ViewResourceLoader;

public class ApplicationContext {

    public final static Map<String, Controller> controllerMap = Map.of(
            "/login", new UserController(),
            "/", new DefaultController()
    );

    public final static HandlerMapping HANDLER_MAPPING = new HandlerMapping(controllerMap);
    public final static ResourceLoader STATIC_RESOURCE_MAPPER = new StaticResourceLoader();
    public final static ViewResolver VIEW_RESOLVER = new ViewResolver(new ViewResourceLoader());
    public final static ExceptionHandler EXCEPTION_HANDLER = new ExceptionHandler(VIEW_RESOLVER);

    public final static List<RequestHandler> REQUEST_HANDLERS = List.of(
            new StaticResourceRequestHandler(STATIC_RESOURCE_MAPPER),
            new ControllerHandler(HANDLER_MAPPING, VIEW_RESOLVER)
    );
}
