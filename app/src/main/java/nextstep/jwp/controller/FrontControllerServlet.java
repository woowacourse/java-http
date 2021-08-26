package nextstep.jwp.controller;

import nextstep.jwp.HttpRequest;
import nextstep.jwp.HttpResponse;

public class FrontControllerServlet {
    private final HttpRequest request;

    public FrontControllerServlet(HttpRequest request) {
        this.request = request;
    }

    public HttpResponse process() {
        Controller controller = findControllerByHttpURIPath();
        if (request.extractHttpMethod().equals("GET")) {
            return controller.get(request);
        }
        throw new RuntimeException();
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
