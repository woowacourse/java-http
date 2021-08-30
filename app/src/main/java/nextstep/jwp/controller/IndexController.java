package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.requestline.RequestURI;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;

public class IndexController extends AbstractController {

    public static final String INDEX_RESOURCE_PATH = "/index.html";
    private static final String INDEX_PATH = "/index";

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        return HttpResponse.of(HttpStatus.OK, new RequestURI(INDEX_RESOURCE_PATH));
    }

    @Override
    protected String requestURI() {
        return INDEX_PATH;
    }
}
