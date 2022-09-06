package nextstep.jwp.controller;

import org.apache.catalina.servlets.AbstractController;
import org.apache.coyote.http11.ResourceLocator;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class StaticResourceController extends AbstractController {

    public StaticResourceController(ResourceLocator resourceLocator) {
        super(resourceLocator);
    }

    @Override
    public boolean isProcessable(HttpRequest request) {
        return request.isStaticResourcePath();
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        doHtmlResponse(response, request.getPathString());
    }
}
