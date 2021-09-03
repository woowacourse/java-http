package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.requestline.RequestPath;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;

public class IndexController extends AbstractController {

    public static final String INDEX_RESOURCE_PATH = "/index.html";

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return HttpResponse.of(HttpStatus.OK, new RequestPath(INDEX_RESOURCE_PATH));
    }
}
