package org.apache.catalina.controller;

import java.util.Set;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerRegistry {

    private static final Logger log = LoggerFactory.getLogger(ControllerRegistry.class);
    private static final RequestMapping REQUEST_MAPPING = RequestMapping.getInstance();

    public static void registerControllers(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends AbstractController>> controllerClasses = reflections.getSubTypesOf(AbstractController.class);

        controllerClasses.forEach(controllerClass -> {
            try {
                AbstractController controllerInstance = controllerClass.getDeclaredConstructor()
                        .newInstance();
                controllerInstance.getEndpoints()
                        .forEach((endpoint) -> REQUEST_MAPPING.registerEndPoint(endpoint, controllerInstance));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}
