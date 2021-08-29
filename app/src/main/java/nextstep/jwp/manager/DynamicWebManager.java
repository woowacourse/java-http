package nextstep.jwp.manager;

import nextstep.jwp.manager.annotation.Controller;
import nextstep.jwp.manager.annotation.GetMapping;
import nextstep.jwp.manager.annotation.PostMapping;
import nextstep.jwp.request.ClientRequest;
import org.reflections.Reflections;

import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DynamicWebManager {

    private final Set<Object> controllers = new HashSet<>();
    private final Map<ClientRequest, Map<Object, Method>> dynamicWebHandler = new HashMap<>();

    public DynamicWebManager() {
        initializeControllers();
        loadControllerHandler();
    }

    private void initializeControllers() {
        Reflections reflections = new Reflections("nextstep.jwp.application");
        final Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
        for (Class<?> controller : controllers) {
            registerController(controller);
        }
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
        for (Object controller : controllers) {
            final Class<?> controllerClass = controller.getClass();
            final Method[] methods = controllerClass.getMethods();
            for (Method method : methods) {
                registerHandler(controller, method);
            }
        }
    }

    private void registerHandler(Object controller, Method method) {
        final GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (!Objects.isNull(getMapping)) {
            final String requestUrl = getMapping.value();
            dynamicWebHandler.put(ClientRequest.of("GET", requestUrl), Collections.singletonMap(controller, method));
        }

        final PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (!Objects.isNull(postMapping)) {
            final String requestUrl = postMapping.value();
            dynamicWebHandler.put(ClientRequest.of("POST", requestUrl), Collections.singletonMap(controller, method));
        }
    }

    public boolean canHandle(ClientRequest clientRequest) {
        return dynamicWebHandler.containsKey(clientRequest);
    }

    public String handle(ClientRequest clientRequest) {
        final Map<Object, Method> handler = dynamicWebHandler.get(clientRequest);
        final Object controller = handler.keySet().iterator().next();
        final Method method = handler.get(controller);
        try {
            final Object result = method.invoke(controller, clientRequest.getQueryParam());
            return String.valueOf(result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("알맞지 않은 요청 양식입니다.");
        }
    }
}
