package nextstep.web;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.web.AbstractController;
import org.apache.coyote.http11.web.View;

public class IndexController extends AbstractController {

    @Override
    public View handleGetRequest(final HttpRequest request, final HttpResponse response) {
        return new View("index.html");
    }
}
