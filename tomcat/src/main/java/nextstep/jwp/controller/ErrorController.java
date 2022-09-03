package nextstep.jwp.controller;

import nextstep.jwp.support.ResourceRegistry;
import org.apache.coyote.servlet.response.HttpResponse;
import org.apache.coyote.support.HttpException;

public class ErrorController {

    private final ResourceRegistry resourceRegistry;

    public ErrorController(ResourceRegistry resourceRegistry) {
        this.resourceRegistry = resourceRegistry;
    }

    public HttpResponse handle(HttpException exception) {
        return resourceRegistry.findErrorPage(exception);
    }
}
