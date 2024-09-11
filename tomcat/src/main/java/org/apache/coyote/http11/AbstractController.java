package org.apache.coyote.http11;

import java.io.IOException;

import org.apache.coyote.Controller;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        if (request.hasMethod(HttpMethod.GET)) {
            doGet(request, response);
        }
        if (request.hasMethod(HttpMethod.POST)) {
            doPost(request, response);
        }

        response.setSourceCode(request.getResources());
        response.putHeader(HttpHeaderField.CONTENT_LENGTH.getValue(), request.getContentLength());
        response.putHeader(HttpHeaderField.CONTENT_TYPE.getValue(), request.getContentTypeToResponseText());
    }

    protected abstract void doPost(HttpRequest request, HttpResponse response);

    protected abstract void doGet(HttpRequest request, HttpResponse response);
}
