package nextstep.jwp.controller;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.http.CustomException;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public class FrontControllerServlet {

    private static final List<Controller> controllers = Arrays.asList(
        new LoginController(),
        new RegisterController()
    );
    private final HttpRequest request;

    public FrontControllerServlet(HttpRequest request) {
        this.request = request;
    }

    public HttpResponse process() {
        Controller controller = findControllerByHttpURIPath();
        if (request.extractHttpMethod() == HttpMethod.GET) {
            return controller.get(request);
        }
        if (request.extractHttpMethod() == HttpMethod.POST) {
            return controller.post(request);
        }
        throw new CustomException("유효하지 않은 HTTP Method입니다.");
    }

    private Controller findControllerByHttpURIPath() {
        return controllers.stream()
            .filter(controller -> controller.isSatisfiedBy(request.extractURIPath()))
            .findFirst()
            .orElseGet(NoController::new);
    }
}
