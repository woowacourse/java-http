package org.apache.coyote.componentscan;

import java.util.HashSet;
import java.util.Set;
import org.apache.coyote.annotation.Controller;

public class ControllerScanner {

    public static Set<Class<?>> scan() {
        final Set<Class<?>> controllers = new HashSet<>();
        final Set<Class<?>> components = ComponentScanner.scan();
        for (final Class<?> component : components) {
            if (component.isAnnotationPresent(Controller.class)) {
                controllers.add(component);
            }
        }
        return controllers;
    }
}
