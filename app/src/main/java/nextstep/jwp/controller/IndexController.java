package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public class IndexController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return create200Response(request);
    }
}
