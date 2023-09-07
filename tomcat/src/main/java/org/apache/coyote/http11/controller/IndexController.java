package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StaticResource;

public class IndexController extends AbstractController {
    private static final Uri indexUri = Uri.INDEX;

    @Override
    public boolean canHandle(final HttpRequest request) {
        final String path = request.getRequestLine().getPath();
        return path.startsWith(indexUri.getSimplePath());
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        final StaticResource staticResource = StaticResource.from(indexUri.getFullPath());
        final ResponseBody responseBody = ResponseBody.from(staticResource);
        return HttpResponse.of(HttpStatus.OK, responseBody);
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        throw new UnsupportedOperationException();
    }
}
