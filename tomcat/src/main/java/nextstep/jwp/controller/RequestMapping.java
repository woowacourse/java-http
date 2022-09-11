package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.function.Predicate;

import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public enum RequestMapping {
    RESOURCE(HttpRequest::isResource, new ResourceController()),
    LOGIN(request -> request.compareUrl("/login"), new LoginController()),
    REGISTER(request -> request.compareUrl("/register"), new RegisterController()),
    BASE(request -> request.compareUrl("/"), new BaseController());

    private final Predicate<HttpRequest> predicate;
    private final Controller controller;

    RequestMapping(Predicate<HttpRequest> predicate, Controller controller) {
        this.predicate = predicate;
        this.controller = controller;
    }

    public static Controller mapping(HttpRequest request) throws Exception {
        return Arrays.stream(values())
                .filter(mapping -> mapping.predicate.test(request))
                .map(mapping -> mapping.controller)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 url입니다."));
    }
}
