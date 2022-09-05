package nextstep.jwp.controller;

import nextstep.jwp.servlet.handler.Controller;
import nextstep.jwp.servlet.handler.RequestMapping;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse;

@RequestMapping(path = {"/", "/index"})
public class HomeController extends Controller {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.ok().setViewResource("/index.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        throw new UnsupportedOperationException("API not implemented");
    }
}
