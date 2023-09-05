package org.apache.coyote;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceController extends Controller {

    @Override
    public boolean canHandle(final HttpRequest target) {
        return true;
    }

    @Override
    public HttpResponse doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        return null;
    }

    @Override
    public HttpResponse doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        return null;
    }
}
