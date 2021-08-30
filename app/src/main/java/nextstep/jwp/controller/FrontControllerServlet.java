package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public class FrontControllerServlet {

    private static final List<AbstractController> controllers = Arrays.asList(
        new LoginController(),
        new RegisterController()
    );
    private final HttpRequest request;
    private final HttpResponse response;

    public FrontControllerServlet(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
    }

    public void process() {
        AbstractController controller = findControllerByHttpURIPath();
        controller.service(request, response);
    }

    private AbstractController findControllerByHttpURIPath() {
        return controllers.stream()
            .filter(controller -> controller.isSatisfiedBy(request.extractURIPath()))
            .findFirst()
            .orElseGet(NoController::new);
    }
}
