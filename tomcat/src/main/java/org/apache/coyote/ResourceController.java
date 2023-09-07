package org.apache.coyote;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

public class ResourceController extends Controller {

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        return "GET".equals(httpRequest.getMethod());
    }

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        handleResource(httpRequest.getTarget(), httpRequest, httpResponse);
    }

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
    }
}
