package nextstep.jwp.framework.infrastructure.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.framework.domain.annotation.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationContextLoader {

    private static final Logger log = LoggerFactory.getLogger(ApplicationContextLoader.class);

    private ApplicationContextLoader() {
    }

    public static List<Class<?>> loadBeans(String packageRootName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String packageRootPath = packageRootName.replace('.', '/');
        URL resource = classLoader.getResource(packageRootPath);
        File packageRootDirectory = new File(resource.getFile());
        try {
            return findBean(packageRootDirectory, packageRootName);
        } catch (ClassNotFoundException classNotFoundException) {
            log.error("Bean File Load Error", classNotFoundException);
            throw new RuntimeException(classNotFoundException);
        }
    }

    private static List<Class<?>> findBean(
        File directory,
        String packageName
    ) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            examineFileAttribute(file, classes, packageName);
        }
        return classes;
    }

    private static void examineFileAttribute(File file,
        List<Class<?>> classes,
        String packageName
    ) throws ClassNotFoundException {
        if (file.isDirectory()) {
            classes.addAll(findBean(file, packageName + "." + file.getName()));
            return;
        }
        if (file.getName().endsWith(".class")) {
            String className = parseClassName(file, packageName);
            Class<?> target = Class.forName(className);
            addOnCondition(classes, target);
        }
    }

    private static void addOnCondition(List<Class<?>> classes, Class<?> target) {
        if (!target.isAnnotation() && target.isAnnotationPresent(Controller.class)) {
            classes.add(target);
        }
    }

    private static String parseClassName(File file, String packageName) {
        String classFullName = file.getName();
        String classSimpleName = classFullName.substring(0, classFullName.length() - 6);
        return packageName + "." + classSimpleName;
    }
}
