package nextstep.jwp.controller;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class ResourceController extends AbstractController {

    private static final ResourceController INSTANCE = new ResourceController();

    private ResourceController() {
    }

    public static ResourceController getInstance() {
        return INSTANCE;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.init(httpRequest.getVersion(), HttpStatusCode.OK)
                .setBodyByPath(httpRequest.getPath());
    }

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest) {
        return HttpResponse.init(httpRequest.getVersion(), HttpStatusCode.METHOD_NOT_ALLOWED)
                .setBody(HttpStatusCode.METHOD_NOT_ALLOWED.getMessage());
    }
}
