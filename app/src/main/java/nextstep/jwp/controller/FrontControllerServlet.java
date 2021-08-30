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
    private final HttpResponse response;

    public FrontControllerServlet(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
    }

    public void process() {
        Controller controller = findControllerByHttpURIPath();
        if (request.extractHttpMethod() == HttpMethod.GET) {
            controller.get(request, response);
            return;
        }
        if (request.extractHttpMethod() == HttpMethod.POST) {
            controller.post(request, response);
            return;
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
