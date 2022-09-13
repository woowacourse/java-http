package nextstep.jwp.controller;

import org.apache.catalina.servlet.RequestMapping;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class ResourceController extends AbstractController {

    public ResourceController() {
        super(RequestMapping.DEFAULT_URL);
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        response.ok().setViewResource(request.getUri());
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        methodNotFound(response);
    }
}
