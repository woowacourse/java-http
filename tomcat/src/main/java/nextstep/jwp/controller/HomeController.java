package nextstep.jwp.controller;

import java.util.List;
import nextstep.jwp.exception.ExceptionHandler;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class HomeController extends AbstractController {

    public HomeController(ExceptionHandler exceptionHandler) {
        super(List.of("/", "/index"), exceptionHandler);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.ok().setViewResource(Page.INDEX.getUri());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        methodNotFound(response);
    }
}
