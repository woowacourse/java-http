package com.techcourse;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import org.apache.catalina.startup.Tomcat;
import org.apache.controller.AbstractController;
import org.apache.controller.HandlerContainer;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

public class Application {

    public static void main(String[] args)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Reflections reflections = new Reflections(Application.class.getPackage().getName(), new SubTypesScanner(false));

        Set<Class<? extends AbstractController>> classes = reflections.getSubTypesOf(AbstractController.class);

        for (Class<? extends AbstractController> clazz : classes) {
            Constructor<? extends AbstractController> constructor = clazz.getConstructor();
            AbstractController handler = constructor.newInstance();
            HandlerContainer.add(handler);
        }

        final var tomcat = new Tomcat();
        tomcat.start();
    }
}
