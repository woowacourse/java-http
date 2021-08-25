package nextstep.jwp.framework.infrastructure.config;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.framework.domain.NetworkHandler;
import nextstep.jwp.framework.domain.annotation.GetMapping;
import nextstep.jwp.framework.domain.annotation.PostMapping;
import nextstep.jwp.framework.infrastructure.HttpHandler;
import nextstep.jwp.framework.infrastructure.adapter.RequestAdapter;
import nextstep.jwp.framework.infrastructure.adapter.get.PageGetRequestAdapter;
import nextstep.jwp.framework.infrastructure.adapter.post.LoginRequestAdapter;
import nextstep.jwp.framework.infrastructure.adapter.post.RegisterRequestAdapter;
import nextstep.jwp.framework.infrastructure.mapping.HttpRequestMapping;
import nextstep.jwp.framework.infrastructure.mapping.Key;
import nextstep.jwp.framework.infrastructure.mapping.RequestMapping;
import nextstep.jwp.framework.infrastructure.resolver.StaticFileResolver;
import nextstep.jwp.web.presentation.PageController;
import nextstep.jwp.web.presentation.UserController;

public class FactoryConfiguration {

    private FactoryConfiguration() {
    }

    public static NetworkHandler networkHandler() {
        return new HttpHandler(requestMapping());
    }

    public static RequestMapping requestMapping() {
        return new HttpRequestMapping(adapters());
    }

    public static Map<Key, RequestAdapter> adapters() {
        Map<Key, RequestAdapter> adapters = new HashMap<>();

        Key getIndex = new Key("/", GetMapping.class);
        Key getLogin = new Key("/login", GetMapping.class);
        Key getRegister = new Key("/register", GetMapping.class);
        Key postLogin = new Key("/login", PostMapping.class);
        Key postRegister = new Key("/register", PostMapping.class);

        adapters.put(getIndex, generateGetRequestAdapter("/", PageController.class));
        adapters.put(getLogin, generateGetRequestAdapter("/login", PageController.class));
        adapters.put(getRegister, generateGetRequestAdapter("/register", PageController.class));
        adapters.put(postLogin, generatePostRequestAdapter("/login", UserController.class));
        adapters.put(postRegister, generatePostRequestAdapter("/register", UserController.class));
        return adapters;
    }

    private static RequestAdapter generateGetRequestAdapter(String url, Class<?> controller) {
        Method target = Arrays.stream(controller.getMethods())
            .filter(method -> method.isAnnotationPresent(GetMapping.class))
            .filter(method -> method.getAnnotation(GetMapping.class).value().equals(url))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("컨트롤러 설정 실패"));
        return new PageGetRequestAdapter(controller, target, StaticFileResolver.getInstance());
    }

    private static RequestAdapter generatePostRequestAdapter(String url, Class<?> controller) {
        Method target = Arrays.stream(controller.getMethods())
            .filter(method -> method.isAnnotationPresent(PostMapping.class))
            .filter(method -> method.getAnnotation(PostMapping.class).value().equals(url))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("컨트롤러 설정 실패"));
        if (url.equals("/login")) {
            return new LoginRequestAdapter(controller, target, StaticFileResolver.getInstance());
        }
        if (url.equals("/register")) {
            return new RegisterRequestAdapter(controller, target, StaticFileResolver.getInstance());
        }
        throw new IllegalStateException("컨트롤러 설정 실패");
    }
}
