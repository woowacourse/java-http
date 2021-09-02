package nextstep.jwp.web.controller;

import nextstep.jwp.web.network.request.HttpRequest;
import nextstep.jwp.web.network.response.HttpResponse;

public abstract class AbstractController implements Controller {

    private final String resource;

    protected AbstractController(String resource) {
        this.resource = resource;
    }

    @Override
    public final String getResource() {
        return resource;
    }

    @Override
    public final void service(HttpRequest request, HttpResponse response) {
        if (request.isGet()) {
            doGet(request, response);
        }
        if (request.isPost()) {
            doPost(request, response);
        }
        // BAD REQUEST? NOT FOUND?
    }

    protected void doGet(HttpRequest request, HttpResponse response) {
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }
}
