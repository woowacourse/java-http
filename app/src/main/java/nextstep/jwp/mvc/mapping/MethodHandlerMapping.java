package nextstep.jwp.mvc.mapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.core.ApplicationContext;
import nextstep.jwp.mvc.annotation.Controller;
import nextstep.jwp.mvc.annotation.RequestMapping;
import nextstep.jwp.mvc.annotation.ResponseBody;
import nextstep.jwp.mvc.argument.ArgumentResolverContainer;
import nextstep.jwp.mvc.handler.Handler;
import nextstep.jwp.mvc.handler.MethodHandler;
import nextstep.jwp.mvc.handler.RestControllerHandler;
import nextstep.jwp.mvc.handler.SimpleControllerHandler;
import nextstep.jwp.webserver.request.HttpRequest;

public class MethodHandlerMapping implements HandlerMapping {

    private ApplicationContext applicationContext;
    private final List<Handler> methodHandlers;
    private final ArgumentResolverContainer argumentResolverContainer;

    public MethodHandlerMapping(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.argumentResolverContainer = new ArgumentResolverContainer();
        final List<Object> controllers =
                applicationContext.getBeansWithAnnotation(Controller.class);
        this.methodHandlers = mapToMethodHandlers(controllers);
    }

    private List<Handler> mapToMethodHandlers(List<Object> controllers) {
        List<Handler> handlers = new ArrayList<>();
        for (Object controller : controllers) {
            for (Method method : controller.getClass().getMethods()) {
                if(method.isAnnotationPresent(RequestMapping.class)) {
                    final MethodHandler methodHandler;
                    if(method.isAnnotationPresent(ResponseBody.class)) {
                        methodHandler = new RestControllerHandler(method, controller, argumentResolverContainer);
                    } else {
                        methodHandler = new SimpleControllerHandler(method, controller, argumentResolverContainer);
                    }
                    handlers.add(methodHandler);
                }
            }
        }
        return handlers;
    }

    @Override
    public Handler findHandler(HttpRequest httpRequest) {
        return findMethodHandler(methodHandlers, httpRequest);
    }

    private Handler findMethodHandler(List<Handler> controllers, HttpRequest httpRequest) {
        for (Handler controller : controllers) {
            if (controller.matchUrl(httpRequest.httpUrl(), httpRequest.httpMethod())) {
                return controller;
            }
        }
        return null;
    }
}
