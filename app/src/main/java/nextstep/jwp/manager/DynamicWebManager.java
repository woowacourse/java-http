package nextstep.jwp.manager;

import nextstep.jwp.manager.annotation.Controller;
import nextstep.jwp.manager.annotation.GetMapping;
import nextstep.jwp.manager.annotation.PostMapping;
import nextstep.jwp.manager.annotation.RequestParameter;
import nextstep.jwp.request.ClientRequest;
import nextstep.jwp.request.HttpMethod;
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
    private final Map<ClientRequest, Map<Object, Method>> dynamicWebHandler = new HashMap<>();

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

    private void registerController(Class<?> bean) {
        final Constructor<?> constructor;
        try {
            constructor = bean.getConstructor(null);
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
            dynamicWebHandler.put(ClientRequest.of(HttpMethod.GET, requestUrl), Collections.singletonMap(controller, method));
        }
    }

    private void registerPostMethod(Object controller, Method method) {
        final PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (!Objects.isNull(postMapping)) {
            final String requestUrl = postMapping.value();
            dynamicWebHandler.put(ClientRequest.of(HttpMethod.POST, requestUrl), Collections.singletonMap(controller, method));
        }
    }

    public boolean canHandle(ClientRequest clientRequest) {
        return dynamicWebHandler.containsKey(clientRequest);
    }

    public String handle(ClientRequest clientRequest) {
        final Map<Object, Method> handler = dynamicWebHandler.get(clientRequest);
        final Object controller = handler.keySet().iterator().next();
        final Method method = handler.get(controller);
        final List<String> methodParameters = mapMethodParameters(clientRequest, method);

        try {
            final Object result = method.invoke(controller, methodParameters.toArray(new String[0]));
            return String.valueOf(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("알맞지 않은 요청 양식입니다.");
        }
    }

    private List<String> mapMethodParameters(ClientRequest clientRequest, Method method) {
        final Parameter[] parameters = method.getParameters();
        return Arrays.stream(parameters)
                .map(parameter -> parameter.getAnnotation(RequestParameter.class))
                .filter(requestParameter -> !Objects.isNull(requestParameter))
                .map(RequestParameter::value)
                .map(clientRequest::searchRequestBody)
                .collect(Collectors.toList());
    }
}
