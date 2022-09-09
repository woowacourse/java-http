package nextstep.jwp.ui;

import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.model.HttpRequest;
import org.apache.coyote.http11.model.HttpRequestURI;
import org.apache.coyote.http11.model.HttpResponse;
import org.apache.coyote.http11.model.HttpStatusCode;

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
        return redirectTo(HttpRequestURI.from("/404"), HttpStatusCode.HTTP_STATUS_NOT_FOUND);
    }
}
