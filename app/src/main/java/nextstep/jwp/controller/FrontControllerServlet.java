package nextstep.jwp.controller;

import nextstep.jwp.HttpRequest;
import nextstep.jwp.HttpResponse;

public class FrontControllerServlet {
    private final HttpRequest request;
    private final HttpResponse response;

    public FrontControllerServlet(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
    }

    public void process() {
        Controller controller = findControllerByHttpURIPath();
        if (request.extractHttpMethod().equals("GET")) {
            controller.get(request, response);
            return;
        }
        if (request.extractHttpMethod().equals("POST")) {
            controller.post(request, response);
            return;
        }
        if (request.extractHttpMethod().equals("PUT")) {
            controller.put(request, response);
            return;
        }
        if (request.extractHttpMethod().equals("PATCH")) {
            controller.patch(request, response);
            return;
        }
        if (request.extractHttpMethod().equals("DELETE")) {
            controller.delete(request, response);
            return;
        }
    }

    private Controller findControllerByHttpURIPath() {
        if (request.extractURIPath().equals("/login")) {
            return new LoginController();
        }
        return new NoController();
    }
}
