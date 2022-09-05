package nextstep.jwp.controller;

import nextstep.jwp.servlet.handler.Controller;
import nextstep.jwp.servlet.handler.RequestMapping2;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse2;

@RequestMapping2(path = {"/", "/index"})
public class HomeController2 extends Controller {

    @Override
    protected void doGet(HttpRequest request, HttpResponse2 response) {
        response.ok().setViewResource("/index.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse2 response) {
        throw new UnsupportedOperationException("API not implemented");
    }
}
