package org.apache.mvc;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.mvc.annotation.RequestMapping;
import org.apache.mvc.handlerchain.RequestHandlerMethod;
import org.apache.mvc.handlerchain.RequestKey;

public class ControllerParser {

    public static Map<RequestKey, RequestHandlerMethod> parse(List<Controller> controllers) {
        return controllers.stream()
                .flatMap(controller -> dismantleMethod(controller).entrySet().stream())
                .collect(Collectors.toMap(
                        Entry::getKey,
                        Entry::getValue
                ));
    }

    private static Map<RequestKey, RequestHandlerMethod> dismantleMethod(Controller controller) {
        return Arrays.stream(controller.getClass().getDeclaredMethods())
                .filter(method -> findRequestMappedMethod(method) != null)
                .collect(Collectors.toMap(
                        method -> RequestKey.from(findRequestMappedMethod(method)),
                        method -> new RequestHandlerMethod(controller, method)
                ));
    }

    private static RequestMapping findRequestMappedMethod(Method method) {
        return method.getDeclaredAnnotation(RequestMapping.class);
    }
}
