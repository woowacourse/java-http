package nextstep.jwp.controller;

import org.apache.catalina.servlet.AbstractController;
import org.apache.catalina.servlet.exception.MethodNotAllowedException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;

public class HomeController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        throw new MethodNotAllowedException();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.setResponseBody(ResponseBody.of("Hello world!", "html"));
    }
}
