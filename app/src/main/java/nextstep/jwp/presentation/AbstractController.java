package nextstep.jwp.presentation;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.Method;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.content.ContentType;
import nextstep.jwp.http.response.status.HttpStatus;

public abstract class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        Method method = request.getMethod();

        if (method == Method.GET) {
            doGet(request, response);
        }
        if (method == Method.POST) {
            doPost(request, response);
        }
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doPost(HttpRequest request, HttpResponse response) {
    }

    protected void renderPage(HttpRequest request, HttpResponse response, String resource) {
        response.setStatusLine(request.getProtocolVersion(), HttpStatus.OK);
        response.addResponseHeader("Content-Type", ContentType.HTML.getType());
        response.addResponseHeader("Content-Length", String.valueOf(resource.getBytes().length));
        response.setResponseBody(resource);
    }

    protected void redirectPage(HttpRequest request, HttpResponse response, HttpStatus httpStatus, String filePath) {
        response.setStatusLine(request.getProtocolVersion(), httpStatus);
        response.addResponseHeader("Location", filePath);
    }
}
