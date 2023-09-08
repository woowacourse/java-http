package nextstep.jwp.controller;

import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.RequestHandler;
import org.apache.coyote.http.LoginManager;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

import java.util.HashSet;
import java.util.Set;

import static nextstep.jwp.controller.Path.NOT_FOUND;

public class FrontHandler implements RequestHandler {

    private final Set<Controller> requestControllers = new HashSet<>();

    public FrontHandler(final LoginManager loginManager) {
        requestControllers.add(new HomePageController());
        requestControllers.add(new StaticFileController());
        requestControllers.add(new LoginController(loginManager));
        requestControllers.add(new RegisterController());
    }

    @Override
    public void handle(final HttpRequest request, final HttpResponse response) throws Exception {
        for (final Controller controller : requestControllers) {
            if (controller.supports(request)) {
                controller.service(request, response);
                return;
            }
        }

        response.mapToRedirect(NOT_FOUND.getPath());
    }
}
