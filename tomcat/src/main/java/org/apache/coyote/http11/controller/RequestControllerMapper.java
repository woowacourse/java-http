package org.apache.coyote.http11.controller;

import nextstep.jwp.model.Request;
import nextstep.jwp.vo.RequestMethod;

import java.util.Arrays;
import java.util.Optional;

public enum RequestControllerMapper {
    GET_LOGIN(RequestMethod.GET, "/login", SessionedLoginController.getInstance()),
    GET_ROOT(RequestMethod.GET, "/", DefaultController.getInstance()),
    GET_INDEX(RequestMethod.GET, "/index", DefaultController.getInstance()),
    POST_LOGIN(RequestMethod.POST, "/login", LoginController.getInstance()),
    POST_REGISTER(RequestMethod.POST, "/register", RegisterController.getInstance());

    private static final String NO_MAPPED_CONTROLLER = "해당 요청에 매핑되는 컨트롤러가 없습니다.";

    private final RequestMethod method;
    private final String url;
    private final Controller controller;

    RequestControllerMapper(RequestMethod method, String url, Controller controller) {
        this.method = method;
        this.url = url;
        this.controller = controller;
    }

    public static Controller mapByRequest(Request request) {
        Optional<RequestControllerMapper> result = Arrays.stream(RequestControllerMapper.values())
                .filter(each -> request.canMapped(each.method, each.url))
                .findFirst();
        if (result.isPresent()) {
            return result.get().controller;
        }
        return DefaultController.getInstance();
    }
}
