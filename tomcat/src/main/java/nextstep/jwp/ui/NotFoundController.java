package nextstep.jwp.ui;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class NotFoundController extends AbstractController {
    private static final NotFoundController controller = new NotFoundController();

    private NotFoundController() {
    }

    public static NotFoundController getController() {
        return controller;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        return redirectTo("/404", HttpStatusCode.HTTP_STATUS_NOT_FOUND);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) throws Exception {
        return redirectTo("/404", HttpStatusCode.HTTP_STATUS_NOT_FOUND);
    }
}
