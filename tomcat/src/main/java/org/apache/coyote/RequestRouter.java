package org.apache.coyote;

import org.apache.http.ContentType;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ViewResolver;
import org.apache.http.annotation.Controller;
import org.apache.http.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RequestRouter {

    private static final Map<String, Map<String, Method>> mappingMethods;

    static {
        final String basePackageName = "nextstep.jwp.controller";
        final String packagePath = basePackageName.replace('.', '/');
        final List<Class<?>> classes = new ArrayList<>();
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final URL packageUrl = classLoader.getResource(packagePath);
        final File packageDir = new File(packageUrl.getFile());

        if (packageDir.exists() && packageDir.isDirectory()) {
            for (final File file : packageDir.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".class")) {
                    final String className = basePackageName + "." + file.getName().replace(".class", "");
                    try {
                        final Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(Controller.class)) {
                            classes.add(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException("클래스를 찾을 수 없습니다.");
                    }
                }
            }
        }

        final Map<String, Map<String, Method>> methods = new HashMap<>();
        for (final Class<?> controllerClass : classes) {
            for (final Method method : controllerClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    final RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    validateParameters(method);
                    insertMethod(method, methods, requestMapping);
                }
            }
        }

        mappingMethods = Collections.unmodifiableMap(methods);
    }

    private static void validateParameters(final Method method) {
        if (method.getParameterCount() == 2 &&
                method.getParameterTypes()[0] == HttpRequest.class &&
                method.getParameterTypes()[1] == HttpResponse.class) {
            return;
        }

        throw new IllegalStateException("메서드의 파라미터는 (HttpRequest, HttpResponse)여야 합니다.");
    }

    private static void insertMethod(final Method method, final Map<String, Map<String, Method>> methods, final RequestMapping requestMapping) {
        final String path = requestMapping.path();
        final String methodType = requestMapping.method();
        if (!methods.containsKey(methodType)) {
            methods.put(methodType, new HashMap<>());
        }
        if (methods.get(methodType).containsKey(path)) {
            throw new IllegalStateException("중복된 api에 대한 메서드가 존재합니다.");
        }
        methods.get(methodType).put(path, method);
    }

    public void route(final HttpRequest request, final HttpResponse response) throws InvocationTargetException, IllegalAccessException, IOException, NoSuchMethodException, InstantiationException {
        if (mappingMethods.containsKey(request.getHttpMethod()) && mappingMethods.get(request.getHttpMethod()).containsKey(request.getRequestURI())) {
            final Method method = mappingMethods.get(request.getHttpMethod()).get(request.getRequestURI());
            final Class<?> declaringClass = method.getDeclaringClass();
            method.invoke(declaringClass.getConstructor().newInstance(), request, response);
            return;
        }

        response.setHttpStatus(HttpStatus.OK);
        final ViewResolver viewResolver = new ViewResolver(Path.of(request.getRequestURI()));
        response.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.of(viewResolver.getFileExtension()).getValue());
        response.write(Files.readString(viewResolver.getResourcePath()));
    }
}
