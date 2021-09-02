package nextstep.jwp.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import nextstep.jwp.core.annotation.Autowired;
import nextstep.jwp.core.annotation.Component;
import nextstep.jwp.core.exception.NotFoundBeanException;
import nextstep.jwp.mvc.annotation.Controller;
import org.reflections.Reflections;

public class ComponentLoader {

    public static List<BeanDefinition> load(String packageName) {
        final Set<Class<?>> classes =
                new Reflections(packageName).getTypesAnnotatedWith(Component.class);

        List<BeanDefinition> beanDefinitions = new ArrayList<>();

        classes.stream().filter(aClass -> !aClass.isAnnotation())
                .forEach(aClass -> mapToBean(aClass, beanDefinitions));
        return beanDefinitions;
    }

    private static <T> BeanDefinition mapToBean(Class<T> tClass,
            List<BeanDefinition> beanDefinitions) {
        if (!tClass.isAnnotationPresent(Component.class) && !tClass.isAnnotationPresent(Controller.class)) {
            throw new NotFoundBeanException();
        }

        return beanDefinitions.stream().filter(beanDefinition -> beanDefinition.isTypeOf(tClass))
                .findAny()
                .orElseGet(() -> {
                    final BeanDefinition beanDefinition = createBeanDefinition(tClass,
                            beanDefinitions);
                    beanDefinitions.add(beanDefinition);
                    return beanDefinition;
                });
    }

    private static <T> BeanDefinition createBeanDefinition(Class<T> tClass,
            List<BeanDefinition> beanDefinitions) {
        try {
            for (Constructor<?> constructor : tClass.getConstructors()) {
                BeanDefinition beanWithAnnotation = getBeanWithAnnotation(tClass, beanDefinitions, constructor);
                if (beanWithAnnotation != null) {
                    return beanWithAnnotation;
                }
            }
            final T target = tClass.getConstructor((Class<?>[]) null).newInstance();
            return new BeanDefinition(tClass, target);
        } catch (Exception e) {
            throw new NotFoundBeanException();
        }
    }

    private static <T> BeanDefinition getBeanWithAnnotation(Class<T> tClass,
            List<BeanDefinition> beanDefinitions, Constructor<?> constructor)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if (constructor.isAnnotationPresent(Autowired.class)) {
            Object[] parameterTargets = createParameterTargets(constructor,
                    beanDefinitions);
            final Object target = createTarget(constructor, parameterTargets);
            return new BeanDefinition(tClass, target);
        }
        return null;
    }

    private static Object[] createParameterTargets(Constructor<?> constructor,
            List<BeanDefinition> beanDefinitions) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        final Object[] parameterTargets = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            final Class<?> parameterType = parameterTypes[i];

            final BeanDefinition definition = beanDefinitions.stream()
                    .filter(beanDefinition -> beanDefinition.isTypeOf(parameterType))
                    .findAny()
                    .orElseGet(() -> mapToBean(parameterType, beanDefinitions));

            parameterTargets[i] = definition.getTarget();
        }
        return parameterTargets;
    }

    private static Object createTarget(Constructor<?> constructor, Object... parameters)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return constructor.newInstance(parameters);
    }
}
