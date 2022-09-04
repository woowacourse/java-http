package org.apache.coyote.http11.request.mapping.controllerscan;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.coyote.http11.request.mapping.MappingKey;
import org.apache.coyote.http11.request.mapping.RequestHandler;
import org.apache.coyote.http11.request.mapping.RequestMapper;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.support.PureJavaApiReflectionLoader;
import org.apache.support.ReflectionLoader;

public class ControllerScanner {

    public static void scan(final String basePackage) {
        final ReflectionLoader reflectionLoader = new PureJavaApiReflectionLoader();

        final Set<Class<?>> allClasses = reflectionLoader.getClassesFromBasePackage(basePackage);
        final List<Class<?>> controllers = allClasses.stream()
                .filter(clazz -> clazz.isAnnotationPresent(Controller.class))
                .collect(Collectors.toList());

        controllers.forEach(ControllerScanner::registerMappingController);
    }

    private static void registerMappingController(final Class<?> controller) {
        final Method[] declaredMethods = controller.getDeclaredMethods();
        final List<Method> handlers = Arrays.stream(declaredMethods)
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .collect(Collectors.toList());

        handlers.forEach(handler -> requestMappingHandler(handler, controller));
    }

    private static void requestMappingHandler(final Method handler, final Class<?> controller) {
        final RequestMapping requestMapping = handler.getAnnotation(RequestMapping.class);
        RequestMapper.getInstance()
                .registerMapping(
                        new MappingKey(
                                requestMapping.method(), requestMapping.uri()
                        ),
                        makeRequestHandler(controller, handler)
                );
    }

    private static RequestHandler makeRequestHandler(final Class<?> controller, final Method handler) {
        return httpRequest -> {
            try {
                final Object controllerInstance = controller.getConstructor()
                        .newInstance();
                return (HttpResponse) handler.invoke(controllerInstance, httpRequest);
            } catch (IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException("핸들러 메서드 정보를 읽어올 수 없습니다." + handler, e);
            } catch (InvocationTargetException e) {
                throw (RuntimeException) e.getTargetException();
            } catch (InstantiationException e) {
                throw new RuntimeException("컨트롤러 클래스 정보를 읽어올 수 없습니다.", e);
            }
        };
    }
}
