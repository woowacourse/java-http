package nextstep.jwp.controller;

import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.response.StaticResponse;

public class StaticController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final String resourceType = request.resourceType();
        final String path = request.getRequestURLWithoutQuery();
        final StaticResponse staticResponse = new StaticResponse(resourceType, path);
        response.setResponse(staticResponse.response());
    }
}
