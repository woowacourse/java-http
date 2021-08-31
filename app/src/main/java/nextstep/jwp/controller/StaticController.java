package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.StaticResponse;
import nextstep.jwp.util.StaticResources;

public class StaticController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final StaticResources staticResource = StaticResources.translateResourceFromHeader(request);
        final String path = request.getRequestURLWithoutQuery();
        final StaticResponse staticResponse = new StaticResponse(staticResource, path);
        response.setResponse(staticResponse.response());
    }
}
