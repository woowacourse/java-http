package nextstep.jwp.controller;

import nextstep.jwp.infrastructure.http.request.HttpRequest;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.infrastructure.http.response.ResponseLine;
import nextstep.jwp.infrastructure.http.response.StatusCode;

public class HelloController extends AbstractController {

    @Override
    public String uri() {
        return "/";
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setResponseLine(new ResponseLine(StatusCode.OK));
        respondByFile("/hello.html", response);
    }
}
