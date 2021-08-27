package nextstep.jwp.controller;

import nextstep.jwp.HttpMethod;
import nextstep.jwp.HttpRequest;
import nextstep.jwp.util.FilePathFinder;

public class IndexPageController extends AbstractController {

    private static final HttpMethod HTTP_METHOD = HttpMethod.GET;
    private static final String URI_PATH = "/index.html";

    @Override
    HttpMethod httpMethod() {
        return HTTP_METHOD;
    }

    @Override
    String uriPath() {
        return URI_PATH;
    }

    @Override
    public String doService(HttpRequest httpRequest) {
        //TODO : response 객체 도입
        return FilePathFinder.findPath(uriPath());
    }
}
