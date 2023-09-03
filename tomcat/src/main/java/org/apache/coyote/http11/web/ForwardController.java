package org.apache.coyote.http11.web;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

public class ForwardController extends AbstractController {

    private final String forwardPath;

    public ForwardController(final String forwardPath) {
        this.forwardPath = forwardPath;
    }

    @Override
    protected ResponseEntity handleGetRequest(final HttpRequest request, final HttpResponse response) {
        return ResponseEntity.forwardTo(forwardPath);
    }
}
