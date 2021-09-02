package nextstep.jwp.http;

import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.controller.JwpController;
import nextstep.jwp.controller.UserController;
import nextstep.jwp.controller.ResourceController;

import java.util.Map;

public class RequestMapping {
    private static final Map<String, AbstractController> requestMapper = Map.of(
            "index", new JwpController(),
            "401", new JwpController(),
            "404", new JwpController(),
            "500", new JwpController(),
            "login", new UserController(),
            "register", new UserController());

    public AbstractController getAbstractController(final HttpRequest httpRequest) {
        return mapController(httpRequest);
    }

    private AbstractController mapController(final HttpRequest httpRequest) {
        HttpContentType httpContentType = HttpContentType.matches(httpRequest.getUrl());
        if (!httpContentType.equals(HttpContentType.NOTHING)) {
            return new ResourceController(httpContentType);
        }
        return requestMapper.entrySet().stream()
                .filter(mapper -> httpRequest.containsFunctionInUrl(mapper.getKey()))
                .map(Map.Entry::getValue)
                .findAny()
                .orElseGet(JwpController::new);
    }
}
