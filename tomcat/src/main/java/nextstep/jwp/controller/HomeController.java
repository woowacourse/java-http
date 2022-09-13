package nextstep.jwp.controller;

import java.util.List;
import nextstep.jwp.view.Page;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class HomeController extends AbstractController {

    public HomeController() {
        super(List.of("/", "/index"));
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
