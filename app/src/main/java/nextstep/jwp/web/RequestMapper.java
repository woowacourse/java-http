package nextstep.jwp.web;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.web.controller.Controller;
import nextstep.jwp.web.controller.HomeController;
import nextstep.jwp.web.controller.LoginController;
import nextstep.jwp.web.controller.RegisterController;

public class RequestMapper {

    private static final Map<Predicate<HttpRequest>, Controller> mapper = new HashMap<>();

    static {
        mapper.put(isAcceptable("/"), new HomeController());
        mapper.put(isAcceptable("/login"), new LoginController());
        mapper.put(isAcceptable("/register"), new RegisterController());
    }

    private static Predicate<HttpRequest> isAcceptable(String uri) {
        return httpRequest -> httpRequest.uri().equals(uri);
    }

    public static Controller findController(HttpRequest request) {
        return mapper.entrySet().stream()
                .filter(e -> e.getKey().test(request))
                .map(Map.Entry::getValue)
                .findAny()
                .orElseThrow(() -> new NotFoundException("Controller가 존재하지 않습니다."));
    }
}
