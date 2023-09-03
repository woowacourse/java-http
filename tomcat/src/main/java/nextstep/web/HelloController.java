package nextstep.web;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.web.AbstractController;

public class HelloController extends AbstractController {

    @Override
    public ResponseEntity handleGetRequest(final HttpRequest request, final HttpResponse response) {
        return ResponseEntity.fromSimpleStringData("Hello world!");
    }
}
