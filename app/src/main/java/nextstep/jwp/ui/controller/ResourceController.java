package nextstep.jwp.ui.controller;

import nextstep.jwp.ui.request.HttpRequest;
import nextstep.jwp.ui.response.HttpResponse;

import java.io.IOException;

public class ResourceController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        try {
            String path = request.getPath();
            return findHttpResponse(request, response, path);
        } catch (Exception e) {
            return response.sendRedirect("/404.html");
        }
    }

    private HttpResponse findHttpResponse(HttpRequest request, HttpResponse response, String path) throws IOException {
        if (path.equals("/500.html")) {
            return response.forward(path, 500);
        }
        if (path.equals("/404.html")) {
            return response.forward(path, 404);
        }
        if (path.equals("/401.html")) {
            return response.forward(path, 401);
        }
        return response.forward(request.getPath());
    }
}
