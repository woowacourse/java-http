package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.ResponseReference;

public class IndexController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return ResponseReference.create200Response(request);
    }
}
