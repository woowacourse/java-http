package nextstep.learning;

import nextstep.jwp.application.controller.UserController;
import nextstep.jwp.framework.manager.annotation.Controller;
import nextstep.jwp.framework.manager.annotation.GetMapping;
import nextstep.jwp.framework.manager.annotation.PostMapping;
import nextstep.jwp.framework.manager.annotation.RequestParameter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

        assertThat(beanClasses).contains("UserController");
    }

    @DisplayName("UserController를 생성하고, 그 안에 GetMapping,PostMapping 어노테이션이 붙은 메서드를 추출할 수 있다.")
    @Test
    void getControllerMethods() {
        Reflections reflections = new Reflections("nextstep.jwp.application");
        final Set<Class<?>> beans = reflections.getTypesAnnotatedWith(Controller.class);

        List<String> getMappingMethods = new ArrayList<>();
        List<String> postMappingMethods = new ArrayList<>();

        for (Class<?> bean : beans) {
            if (bean.getSimpleName().equalsIgnoreCase("UserController")) {
                final Method[] methods = bean.getMethods();
                for (Method method : methods) {
                    if (!Objects.isNull(method.getAnnotation(GetMapping.class))) {
                        getMappingMethods.add(method.getName());
                    }
                    if (!Objects.isNull(method.getAnnotation(PostMapping.class))) {
                        postMappingMethods.add(method.getName());
                    }
                }
            }
        }

        assertThat(getMappingMethods).contains("showLoginPage", "showRegisterPage");
        assertThat(postMappingMethods).contains("loginUser", "registerUser");
    }

    @DisplayName("특정 메서드에 붙은 RequestParameter 어노테이션을 검사할 수 있다.")
    @Test
    void requestParam() throws NoSuchMethodException {
        final Class<UserController> userControllerClass = UserController.class;
        final Method loginUserMethod = userControllerClass.getMethod("loginUser", String.class, String.class);

        final Parameter[] parameters = loginUserMethod.getParameters();
        List<String> parameterValues = new ArrayList<>();
        for (Parameter parameter : parameters) {
            final RequestParameter requestParameter = parameter.getAnnotation(RequestParameter.class);
            if (!Objects.isNull(requestParameter)) {
                parameterValues.add(requestParameter.value());
            }
        }

        assertThat(parameterValues).contains("account", "password");
    }
}
