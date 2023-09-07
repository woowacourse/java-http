package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;

public class RootController extends AbstractController {
    private static final Uri rootUri = Uri.ROOT;

    @Override
    public boolean canHandle(final HttpRequest request) {
        final String path = request.getRequestLine().getPath();
        return path.equals(rootUri.getFullPath());
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return HttpResponse.of(HttpStatus.OK, ResponseBody.noContent(ContentType.HTML));
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        throw new UnsupportedOperationException();
    }
}
