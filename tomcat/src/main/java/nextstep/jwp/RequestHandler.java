package nextstep.jwp;

import java.io.IOException;
import nextstep.jwp.controller.MainController;
import nextstep.jwp.controller.UserController;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {

    private static final String URL_INDEX = "/index.html";
    private static final String URL_LOGIN = "/login";
    private static final String URL_REGISTER = "/register";

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final MainController mainController = new MainController();
    private final UserController userController = new UserController();

    public HttpResponse process(final HttpRequest request) {
        try {
            return match(request);
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
        return HttpResponse.of(Status.INTERNAL_SERVER_ERROR);
    }

    private HttpResponse match(final HttpRequest request) throws IOException {
        if (request.getUrl().contains(".")) {
            return mainController.template(request);
        }
        if (request.getUrl().equals(URL_INDEX)) {
            return mainController.index();
        }
        if (request.getUrl().equals(URL_LOGIN)) {
            return userController.login(request);
        }
        if (request.getUrl().equals(URL_REGISTER)) {
            return userController.register(request);
        }
        return mainController.hello();
    }
}
