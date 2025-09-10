package org.apache.catalina.controller;

import static org.apache.coyote.http11.HttpConstants.INDEX_PAGE;
import static org.apache.coyote.http11.HttpConstants.SLASH;

import org.apache.coyote.http11.dto.request.HttpRequest;

public class DefaultController extends AbstractController {

    private final StaticResourceController staticResourceController;

    public DefaultController(final StaticResourceController staticResourceController) {
        this.staticResourceController = staticResourceController;
    }

    @Override
    protected ControllerResult doGet(final HttpRequest request) {
        return staticResourceController.serve(SLASH + INDEX_PAGE);
    }
}
