package nextstep.jwp.ui;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class IndexController extends AbstractController {

    private static final IndexController controller = new IndexController();

    private IndexController() {
    }

    public static IndexController getController() {
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
