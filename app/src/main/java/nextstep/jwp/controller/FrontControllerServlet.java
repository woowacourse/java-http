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
            return;
        }
        if (request.extractHttpMethod().equals("POST")) {
            controller.post(request);
            return;
        }
        if (request.extractHttpMethod().equals("PUT")) {
            controller.put(request);
            return;
        }
        if (request.extractHttpMethod().equals("PATCH")) {
            controller.patch(request);
            return;
        }
        if (request.extractHttpMethod().equals("DELETE")) {
            controller.delete(request);
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
