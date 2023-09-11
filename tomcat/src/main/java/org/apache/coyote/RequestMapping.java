package org.apache.coyote;

import nextstep.jwp.Controller;
import nextstep.jwp.controller.DefaultController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.ResourceController;
import org.apache.coyote.http11.HttpRequest;

import java.util.Map;
import java.util.function.Predicate;

public class RequestMapping {
    private static final Map<Predicate<HttpRequest>, Controller> CONTROLLER_MAP = Map.of(
            request -> request.getPath().lastIndexOf(".") != -1, new ResourceController(),
            request -> request.getPath().equals("/login"), new LoginController(),
            request -> request.getPath().equals("/register"), new RegisterController(),
            request -> request.getPath().equals("/"), new DefaultController()
    );

    public static Controller getController(HttpRequest request) {
        return CONTROLLER_MAP.entrySet().stream()
                .filter(entry -> entry.getKey().test(request))
                .peek(entry -> System.out.println(entry.getKey().test(request)))
                .peek(entry -> System.out.println(entry.getValue()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경로입니다."));
    }
}
