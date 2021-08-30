package nextstep.jwp;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.RequestMapping;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MethodHandlerMapping {

    Set<Class<?>> controllers = new Reflections("nextstep").getTypesAnnotatedWith(Controller.class);

    public void mappingMethod() {
        List<Method> methods = controllers.stream()
                .flatMap(controller -> Stream.of(controller.getMethods()))
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .collect(Collectors.toList());
    }
}
