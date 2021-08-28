package nextstep.jwp.core.handler.mapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.core.ApplicationContext;
import nextstep.jwp.core.annotation.Controller;
import nextstep.jwp.core.handler.Handler;
import nextstep.jwp.core.handler.MethodHandler;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.request.basic.HttpMethod;

public class MethodHandlerMapping implements HandlerMapping {

    private List<Handler> methodHandlers;

    public MethodHandlerMapping(ApplicationContext applicationContext) {
        final List<Object> controllers =
                applicationContext.getBeansWithAnnotation(Controller.class);
        this.methodHandlers = mapToMethodHandlers(controllers);
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

    private List<Handler> mapToMethodHandlers(List<Object> controllers) {
        List<Handler> handlers = new ArrayList<>();
        for (Object controller : controllers) {
            for (Method method : controller.getClass().getMethods()) {
                if(method.isAnnotationPresent(RequestMapping.class)) {
                    final RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    final MethodHandler methodHandler =
                            new MethodHandler(method, controller, requestMapping.path(), requestMapping.method());
                    handlers.add(methodHandler);
                }
            }
        }
        return handlers;
    }
}
