package org.apache.catalina.controller;

import java.util.Set;
import org.reflections.Reflections;

public class ControllerRegistry {

    private static final String CONTROLLER_PACKAGE = "org.apache.catalina.controller";

    public static void registerControllers() {
        Reflections reflections = new Reflections(CONTROLLER_PACKAGE);
        Set<Class<? extends AbstractController>> controllerClasses = reflections.getSubTypesOf(AbstractController.class);

        controllerClasses.forEach(controllerClass -> {
            try {
                AbstractController controllerInstance = controllerClass
                        .getDeclaredConstructor()
                        .newInstance();
                controllerInstance.getEndpoints().forEach((endpoint) ->
                        RequestMapping.getInstance().registerEndPoint(endpoint, controllerInstance));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
