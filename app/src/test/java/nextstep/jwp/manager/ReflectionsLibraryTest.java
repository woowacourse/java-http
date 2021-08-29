package nextstep.jwp.manager;

import nextstep.jwp.manager.annotation.Controller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ReflectionsLibraryTest {

    @DisplayName("Controller 어노테이션이 붙은 클래스를 추출할 수 있다.")
    @Test
    void getBean() {
        Reflections reflections = new Reflections("nextstep.jwp.application");
        final Set<Class<?>> beans = reflections.getTypesAnnotatedWith(Controller.class);

        final List<String> beanClasses = beans.stream()
                .map(Class::getSimpleName)
                .collect(Collectors.toList());

        assertThat(beanClasses).contains("UserService", "UserRepository", "UserController");
    }

    @DisplayName("UserController를 생성하고, 그 안에 GetMapping,PostMapping 어노테이션이 붙은 메서드를 추출할 수 있따.")
    @Test
    void getControllerMethods() {
        Reflections reflections = new Reflections("nextstep.jwp.application");
        final Set<Class<?>> beans = reflections.getTypesAnnotatedWith(Controller.class);

        for (Class<?> bean : beans) {
            if (bean.getSimpleName().equalsIgnoreCase("UserController")) {
                final Annotation[] annotations = bean.getAnnotations();
                final Method[] methods = bean.getMethods();
                for (Method method : methods) {
                    final Annotation[] annotations1 = method.getAnnotations();
                    for (Annotation annotation : annotations1) {
                        System.out.println("annotation.toString() = " + annotation.toString());
                    }
                }
            }
        }
    }
}
