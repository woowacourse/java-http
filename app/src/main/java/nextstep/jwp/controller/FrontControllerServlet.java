package nextstep.jwp.controller;

import nextstep.jwp.http.CustomException;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

public class FrontControllerServlet {

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
        if (request.extractURIPath().equals("/login")) {
            return new LoginController();
        }
        if (request.extractURIPath().equals("/register")) {
            return new RegisterController();
        }
        return new NoController();
    }
}
