package nextstep.jwp.controller;

import java.util.List;
import nextstep.jwp.exception.ExceptionListener;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class HomeController extends AbstractController {

    public HomeController(ExceptionListener exceptionListener) {
        super(List.of("/", "/index"), exceptionListener);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.ok().setViewResource("/index.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        methodNotFound(response);
    }
}
