package mvc.controller;

import servlet.request.HttpRequest;

public class AbstractPathController extends AbstractController {

    public boolean supports(final HttpRequest request) {
        return false;
    }
}
