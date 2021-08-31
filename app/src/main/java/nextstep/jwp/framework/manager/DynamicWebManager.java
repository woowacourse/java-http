package nextstep.jwp.framework.manager;

import nextstep.jwp.framework.manager.annotation.Controller;
import nextstep.jwp.framework.manager.annotation.GetMapping;
import nextstep.jwp.framework.manager.annotation.PostMapping;
import nextstep.jwp.framework.manager.annotation.RequestParameter;
import nextstep.jwp.framework.http.request.HttpRequest;
import nextstep.jwp.framework.http.request.details.HttpMethod;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class DynamicWebManager {

    private static final Logger log = LoggerFactory.getLogger(DynamicWebManager.class);

    private final Set<Object> controllers = new HashSet<>();
    private final Map<HttpRequest, Map<Object, Method>> dynamicWebHandler = new HashMap<>();

    public DynamicWebManager() {
        initializeControllers();
        loadControllerHandler();
    }

    private void initializeControllers() {
        log.info("*******loading annotated controllers*******");
        Reflections reflections = new Reflections("nextstep.jwp.application");
        final Set<Class<?>> annotatedControllers = reflections.getTypesAnnotatedWith(Controller.class);
        for (Class<?> controller : annotatedControllers) {
            registerController(controller);
        }
        log.info("*******annotated contollers loaded*******");
    }

    private void registerController(Class<?> controller) {
        final Constructor<?> constructor;
        try {
            constructor = controller.getConstructor();
            controllers.add(constructor.newInstance());
        } catch (Exception e) {
            throw new IllegalArgumentException("Controller 생성 실패");
        }
    }

    private void loadControllerHandler() {
        log.info("*******loading controller handlers*******");
        for (Object controller : controllers) {
            final Class<?> controllerClass = controller.getClass();
            final Method[] methods = controllerClass.getMethods();
            for (Method method : methods) {
                registerHandler(controller, method);
            }
        }
        log.info("*******controller handlers loaded*******");
    }

    private void registerHandler(Object controller, Method method) {
        registerGetMethod(controller, method);
        registerPostMethod(controller, method);
    }

    private void registerGetMethod(Object controller, Method method) {
        final GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (!Objects.isNull(getMapping)) {
            final String requestUrl = getMapping.value();
            dynamicWebHandler.put(HttpRequest.of(HttpMethod.GET, requestUrl), Collections.singletonMap(controller, method));
        }
    }

    private void registerPostMethod(Object controller, Method method) {
        final PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (!Objects.isNull(postMapping)) {
            final String requestUrl = postMapping.value();
            dynamicWebHandler.put(HttpRequest.of(HttpMethod.POST, requestUrl), Collections.singletonMap(controller, method));
        }
    }

    public boolean canHandle(HttpRequest httpRequest) {
        return dynamicWebHandler.containsKey(httpRequest);
    }

    public String handle(HttpRequest httpRequest) {
        final Map<Object, Method> handler = dynamicWebHandler.get(httpRequest);
        final Object controller = handler.keySet().iterator().next();
        final Method method = handler.get(controller);
        final List<String> methodParameters = mapMethodParameters(httpRequest, method);

        try {
            final Object result = method.invoke(controller, methodParameters.toArray(new String[0]));
            return String.valueOf(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("해당 컨트롤러 메서드에서 오류가 발생했습니다.");
        }
    }

    private List<String> mapMethodParameters(HttpRequest httpRequest, Method method) {
        final Parameter[] parameters = method.getParameters();
        return Arrays.stream(parameters)
                .map(parameter -> parameter.getAnnotation(RequestParameter.class))
                .filter(requestParameter -> !Objects.isNull(requestParameter))
                .map(RequestParameter::value)
                .map(httpRequest::searchRequestBody)
                .collect(Collectors.toList());
    }
}
