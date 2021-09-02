package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.staticresource.StaticResourceFinder;

import static nextstep.jwp.http.request.HttpMethod.GET;

public class HomeController extends AbstractController {

    public HomeController(StaticResourceFinder staticResourceFinder) {
        super(staticResourceFinder);
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (request.hasMethod(GET)) {
            doGet(request, response);
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        assignStaticResourceByUriToResponse(request, response, ".html");
    }
}
