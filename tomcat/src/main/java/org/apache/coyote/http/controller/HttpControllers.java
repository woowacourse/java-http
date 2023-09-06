package org.apache.coyote.http.controller;

import static java.util.stream.Collectors.toMap;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.util.FileUtil;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpControllers {

    private static final Logger log = LoggerFactory.getLogger(HttpControllers.class);

    private final Map<String, HttpController> controllers;

    private HttpControllers(Map<String, HttpController> controllers) {
        this.controllers = controllers;
    }

    public static HttpControllers readControllers() {
        String packageUri = readPackage();
        Map<String, HttpController> controllers = readHttpControllers(packageUri);
        return new HttpControllers(controllers);
    }

    private static String readPackage() {
        return FileUtil.readStaticFile("class_path").strip();
    }

    private static Map<String, HttpController> readHttpControllers(String packageUri) {
        return new Reflections(packageUri)
            .getSubTypesOf(HttpController.class)
            .stream()
            .collect(toMap(HttpControllers::parsePath, HttpControllers::getInstance));
    }

    private static String parsePath(Class<? extends HttpController> clazz) {
        return clazz.getSimpleName()
                    .replace("Controller", "")
                    .chars()
                    .mapToObj(intVal -> (char) intVal)
                    .map(ch -> {
                        if (Character.isUpperCase(ch)) {
                            return "/" + Character.toLowerCase(ch);
                        }
                        return String.valueOf(ch);
                    })
                    .collect(Collectors.joining());
    }

    private static HttpController getInstance(Class<? extends HttpController> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public Optional<HttpController> get(String path) {
        return Optional.ofNullable(controllers.get(path));
    }
}
