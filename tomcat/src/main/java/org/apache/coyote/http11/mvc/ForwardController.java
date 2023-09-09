package org.apache.coyote.http11.mvc;

import org.apache.coyote.http11.mvc.view.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ForwardController extends AbstractController {

    private final String forwardPath;

    public ForwardController(final String forwardPath) {
        this.forwardPath = forwardPath;
    }

    @Override
    protected ResponseEntity doGetRequest(final HttpRequest request, final HttpResponse response) {
        return ResponseEntity.forwardTo(forwardPath);
    }
}
