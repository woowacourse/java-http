package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class JwpController {
    private final Map<String, Function<String, String>> mappedFunction;

    public JwpController() {
        this.mappedFunction = new HashMap<>();
        this.mappedFunction.put("login", this::login);
    }

    private String login(final String queryString) {
        List<String> params = Arrays.asList(queryString.split("&"));
        User user = UserService.findUser(params);
        return user.toString();
    }

    public String mapResponse(final String request) {
        return this.mappedFunction.keySet().stream()
                .filter(request::contains)
                .map(s -> this.mappedFunction.get(s).apply(request))
                .findAny()
                .orElse("해당 페이지가 존재하지 않습니다.");
    }
}
