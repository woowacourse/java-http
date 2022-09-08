package nextstep.jwp.controller;

import java.util.List;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.request.HttpRequest;

public class HomeController extends AbstractController {

    public HomeController() {
        super(List.of("/", "/index"));
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
