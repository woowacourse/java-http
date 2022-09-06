package nextstep.jwp;

import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.ResourceLocator;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    public RegisterController(ResourceLocator resourceLocator) {
        super(resourceLocator);
    }

    @Override
    public boolean isProcessable(HttpRequest request) {
        return request.isPathEqualTo("/register");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String path = request.getPathString() + ".html";
        doHtmlResponse(response, path);
    }
}
