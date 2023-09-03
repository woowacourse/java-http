package nextstep.web;

import org.apache.coyote.http11.mvc.AbstractController;
import org.apache.coyote.http11.mvc.view.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HelloController extends AbstractController {

    @Override
    public ResponseEntity handleGetRequest(final HttpRequest request, final HttpResponse response) {
        return ResponseEntity.fromSimpleStringData("Hello world!");
    }
}
