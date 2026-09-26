package org.apache.coyote.http11;

import java.io.IOException;

public class StaticResourceController extends AbstractController {

    private final StaticResourceRenderer resourceRenderer;

    public StaticResourceController(final StaticResourceRenderer resourceRenderer) {
        this.resourceRenderer = resourceRenderer;
    }

    @Override
    protected void doGet(
            final HttpRequest request,
            final HttpResponse response
    ) throws IOException {
        resourceRenderer.writeResource(request.path(), response);
    }
}
