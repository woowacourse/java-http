package nextstep.jwp.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;

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
        if (request.extractHttpMethod().equals("POST")) {
            return controller.post(request);
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
