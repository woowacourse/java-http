package nextstep.servlet.controller;

import java.io.IOException;
import java.util.List;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class FrontController {

    private final List<Controller> controllers = List.of(
            new LoginController(),
            new RegisterController(),
            new StaticResourceController()
    );

    public FrontController() {
    }

    public HttpResponse service(HttpRequest request) throws IOException {
        final var controller = controllers.stream()
                                          .filter(candidate -> candidate.canHandle(request))
                                          .findAny()
                                          .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 경로입니다."));
        return controller.handle(request);
    }
}
