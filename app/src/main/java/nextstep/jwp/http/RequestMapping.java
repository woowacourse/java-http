package nextstep.jwp.http;

import nextstep.jwp.controller.AbstractController;
import nextstep.jwp.controller.JwpController;
import nextstep.jwp.controller.ResourceController;
import nextstep.jwp.controller.UserController;

import java.util.Map;

public class RequestMapping {
    private static final AbstractController jwpController = new JwpController();
    private static final AbstractController userController = new UserController();

    private static final Map<String, AbstractController> requestMapper = Map.of(
            "index", jwpController,
            "401", jwpController,
            "404", jwpController,
            "500", jwpController,
            "login", userController,
            "register", userController);

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
