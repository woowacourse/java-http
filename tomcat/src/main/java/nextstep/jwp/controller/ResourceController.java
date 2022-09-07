package nextstep.jwp.controller;

import org.apache.coyote.http.AbstractController;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;

public class ResourceController extends AbstractController {

    private static final ResourceController INSTANCE = new ResourceController();

    private ResourceController() {
    }

    public static ResourceController getInstance() {
        return INSTANCE;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.init(HttpStatusCode.OK)
                .setBodyByPath(httpRequest.getPath());
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        return HttpResponse.init(HttpStatusCode.INTERNAL_SERVER_ERROR)
                .setBodyByPath("/500.html");
    }
}
