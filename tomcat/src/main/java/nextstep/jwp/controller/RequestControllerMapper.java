package nextstep.jwp.controller;

import nextstep.jwp.model.Request;
import nextstep.jwp.vo.RequestMethod;

import java.util.Arrays;

public enum RequestControllerMapper {
    GET_LOGIN(RequestMethod.GET, "/login", SessionedLoginController.getInstance()),
    GET_ROOT(RequestMethod.GET, "/", DefaultController.getInstance()),
    GET_INDEX(RequestMethod.GET, "/index", DefaultController.getInstance()),
    POST_LOGIN(RequestMethod.POST, "/login", LoginController.getInstance()),
    POST_REGISTER(RequestMethod.POST, "/register", RegisterController.getInstance());

    private final RequestMethod method;
    private final String url;
    private final Controller controller;

    RequestControllerMapper(RequestMethod method, String url, Controller controller) {
        this.method = method;
        this.url = url;
        this.controller = controller;
    }

    public static Controller mapByRequest(Request request) {
        return Arrays.stream(RequestControllerMapper.values())
                .filter(each -> request.canMapped(each.method, each.url))
                .map(each -> each.controller)
                .findFirst()
                .orElse(DefaultController.getInstance());
    }
}
