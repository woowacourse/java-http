package nextstep.jwp.controller;

import nextstep.jwp.support.ResourceRegistry;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse;

public class ResourceController {

    private final ResourceRegistry resourceRegistry;

    public ResourceController(ResourceRegistry resourceRegistry) {
        this.resourceRegistry = resourceRegistry;
    }

    public HttpResponse find(HttpRequest request) {
        return resourceRegistry.findStaticResource(request.getUri());
    }
}
