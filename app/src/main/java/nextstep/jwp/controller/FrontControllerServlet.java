package nextstep.jwp.controller;

import nextstep.jwp.HttpRequest;

public class FrontControllerServlet {
    private final HttpRequest request;

    public FrontControllerServlet(HttpRequest request) {
        this.request = request;
    }

    public void process() {
        Controller controller = findControllerByHttpURIPath();
        if (request.extractHttpMethod().equals("GET")) {
            controller.get(request);
        }
        if (request.extractHttpMethod().equals("POST")) {
            controller.post(request);
        }
        if (request.extractHttpMethod().equals("PUT")) {
            controller.put(request);
        }
        if (request.extractHttpMethod().equals("PATCH")) {
            controller.patch(request);
        }
        if (request.extractHttpMethod().equals("DELETE")) {
            controller.delete(request);
        }
    }

    private Controller findControllerByHttpURIPath() {
        if (request.extractURIPath().equals("/login")) {
            return new LoginController();
        }
        return new NoController();
    }
}
