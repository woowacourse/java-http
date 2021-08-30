package nextstep.jwp.app.ui;

import java.io.IOException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;

public class ResourceController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        try {
            String path = request.getPath();
            return findHttpResponse(request, response, path);
        } catch (Exception e) {
            return response.sendRedirect("/404.html", HttpStatus.NOT_FOUND);
        }
    }

    private HttpResponse findHttpResponse(HttpRequest request, HttpResponse response, String path)
            throws IOException {
        if (path.equals("/500.html")) {
            return response.forward(path, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (path.equals("/404.html")) {
            return response.forward(path, HttpStatus.NOT_FOUND);
        }
        if (path.equals("/401.html")) {
            return response.forward(path, HttpStatus.NOT_FOUND);
        }
        return response.forward(request.getPath());
    }
}
