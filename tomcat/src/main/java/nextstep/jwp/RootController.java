package nextstep.jwp;

import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.ResourceLocator;
import org.apache.coyote.http11.response.HttpResponse;

public class RootController extends AbstractController {

    public RootController(ResourceLocator resourceLocator) {
        super(resourceLocator);
    }

    @Override
    public boolean isProcessable(HttpRequest request) {
        return request.isPathEqualTo("/");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        doHtmlResponse(response, "/hello.html");
    }

}
