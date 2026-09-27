package org.apache.coyote.http11;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.controller.Controller;
import org.reflections.Reflections;

public class HandlerMapping {

    private final Map<String, Controller> requestControllerMap;

    public HandlerMapping(final String packagesPath) {
        this.requestControllerMap = scanControllers(packagesPath);
    }

    private Map<String, Controller> scanControllers(final String packagesPath) {
        final Reflections reflections = new Reflections(packagesPath);

        return reflections.getTypesAnnotatedWith(WebController.class)
            .stream()
            .collect(Collectors.toMap(
                type -> type.getAnnotation(WebController.class).path(),
                this::createController
            ));

    }

    private Controller createController(final Class<?> type) {
        try {
            return (Controller) type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException | ClassCastException e) {
            throw new IllegalStateException("컨트롤러를 생성할 수 없습니다: " + type.getName(), e);
        }
    }

    public Optional<Controller> getController(final HttpRequest httpRequest) {
        return Optional.ofNullable(requestControllerMap.get(httpRequest.path()));
    }

}
