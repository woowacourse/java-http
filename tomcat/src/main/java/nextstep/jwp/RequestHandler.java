package nextstep.jwp;

import java.io.IOException;
import java.util.List;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.MainController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.request.Method;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final List<Controller> controllers = List.of(
            new MainController(), new RegisterController(), new LoginController()
    );

    public HttpResponse process(final HttpRequest request) {
        try {
            return match(request);
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
        return HttpResponse.of(Status.INTERNAL_SERVER_ERROR);
    }

    private HttpResponse match(final HttpRequest request) throws IOException {
        Controller controller = getController(request.getUrl());
        if (request.getMethod() == Method.POST) {
            return controller.doPost(request);
        }
        return controller.doGet(request);
    }

    private Controller getController(final String url) {
        return controllers.stream()
                .filter(controller -> controller.isUrlMatches(url))
                .findFirst()
                .orElseGet(MainController::new);
    }
}
