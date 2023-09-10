package org.apache.coyote.controller;

import org.apache.coyote.Controller;
import org.apache.coyote.controller.util.Extension;
import org.apache.coyote.controller.util.FileResolver;
import org.apache.coyote.http11.http.message.HttpRequest;
import org.apache.coyote.http11.http.message.HttpResponse;

import java.io.IOException;

public class FileController implements Controller {

    @Override
    public HttpResponse run(final HttpRequest request) throws IOException {
        final String uri = request.getUri();
        return createResponse(uri);
    }

    public HttpResponse createResponse(final String uri) throws IOException {
        final String messageBody = getResponseBody(uri);
        return HttpResponse.ofOk(Extension.findExtension(uri), messageBody);
    }

    private String getResponseBody(final String uri) throws IOException {
        if (uri.equals("/")) {
            return "Hello world!";
        }
        return FileResolver.readFileToString(uri);
    }
}
