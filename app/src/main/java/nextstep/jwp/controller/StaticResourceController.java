package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.staticresource.StaticResource;

import static nextstep.jwp.http.request.HttpMethod.GET;

public class StaticResourceController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.hasMethod(GET)) {
            doGet(request, response);
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        final RequestLine requestLine = request.getRequestLine();
        final String filePath = requestLine.getUri();
        final StaticResource staticResource = getStaticResource(filePath);
        response.assignStatusCode(200);
        response.addStaticResource(staticResource);
    }
}
