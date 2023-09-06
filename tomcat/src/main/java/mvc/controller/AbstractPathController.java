package mvc.controller;

import org.apache.coyote.http.request.HttpRequest;

public class AbstractPathController extends AbstractController {

    public boolean supports(final HttpRequest request) {
        return false;
    }
}
