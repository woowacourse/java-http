package org.apache.coyote.handle.handler;

import java.io.IOException;
import org.apache.coyote.common.HttpStatus;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class FileHandler implements Handler {

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        viewResolver.renderPage(httpResponse, HttpStatus.OK, httpRequest.getUriPath().substring(1));
    }

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
    }
}
