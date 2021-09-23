package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HomeController;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.request.HttpRequest;

public class RequestMapper {

    private static final Map<String, Controller> mapper = new HashMap<>();

    static {
        mapper.put("/", new HomeController());
        mapper.put("/index.html", new HomeController());
        mapper.put("/login", new LoginController());
        mapper.put("/register", new RegisterController());
    }

    public static Controller map(HttpRequest httpRequest) throws Exception {
        return Optional.ofNullable(mapper.get(httpRequest.getUri()))
                .orElseThrow(() -> new NotFoundException("페이지를 찾을 수 없습니다."));
    }

}
