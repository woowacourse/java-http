package nextstep.jwp.ui;

import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpResponse;
import org.apache.coyote.http11.model.HttpStatusCode;

public class ResourceController extends AbstractController {
    private static final ResourceController controller = new ResourceController();

    private ResourceController() {
    }

    public static ResourceController getController() {
        return controller;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return createGetResponseFrom(request);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws Exception {
        return redirectTo("/404", HttpStatusCode.HTTP_STATUS_NOT_FOUND);
    }
}
