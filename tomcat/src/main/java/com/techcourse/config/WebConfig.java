package com.techcourse.config;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.apache.catalina.Manager;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.routing.Dispatcher;
import org.apache.catalina.routing.RequestRegistry;
import org.apache.catalina.routing.RouteKey;
import org.apache.catalina.routing.controller.Controller;
import org.apache.catalina.routing.requestMapping.RequestMapping;
import org.apache.catalina.session.SessionManager;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11Processor;
import org.reflections.Reflections;

public final class WebConfig {

    public Tomcat tomcat() {
        final Manager manager = new SessionManager();
        final Dispatcher dispatcher = new Dispatcher(requestMapping());
        final Connector connector = new Connector(
                connection -> new Http11Processor(connection, manager, dispatcher)
        );
        return new Tomcat(connector);
    }

    private RequestRegistry requestMapping() {
        final RequestRegistry requestRegistry = new RequestRegistry(new HashMap<>());
        configureRoutes(requestRegistry);
        return requestRegistry;
    }

    private void configureRoutes(final RequestRegistry requestRegistry) {
        List<Object> handlers = getControllers();
        setRegistry(requestRegistry, handlers);
    }

    private List<Object> getControllers() {
        Set<Class<?>> typesAnnotatedWith = new Reflections("com.techcourse.controller")
                .getTypesAnnotatedWith(Controller.class);

        return typesAnnotatedWith.stream()
                .map(this::createController)
                .toList();
    }

    private Object createController(final Class<?> controllerType) {
        try {
            return controllerType.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("컨트롤러 생성 실패: " + controllerType.getName(), e);
        }
    }

    private void setRegistry(RequestRegistry registry, List<Object> handlers) {
        for (Object handler : handlers) {
            for (Method method : handler.getClass().getDeclaredMethods()) {
                RequestMapping mapping = method.getAnnotation(RequestMapping.class);
                if (mapping == null) {
                    continue;
                }
                RouteKey routeKey = new RouteKey(mapping.method(), mapping.path());

                registry.add(routeKey, (request, response) -> {
                    try {
                        return (String) method.invoke(handler, request, response);
                    } catch (ReflectiveOperationException e) {
                        throw new IllegalStateException("처리 메서드 호출 실패: " + method, e);
                    }
                });

            }
        }
    }
}
