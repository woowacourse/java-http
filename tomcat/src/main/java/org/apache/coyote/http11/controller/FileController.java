package org.apache.coyote.http11.controller;

import java.io.IOException;
import org.apache.coyote.http11.Request.HttpRequest;
import org.apache.coyote.http11.Response.HttpResponse;
import org.apache.coyote.http11.model.Path;
import org.apache.coyote.http11.utils.Files;

public class FileController extends AbstractController {

    @Override
    protected HttpResponse doPost(final HttpRequest request) throws IOException {
        return HttpResponse.methodNotAllowed();
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) throws IOException {
        final String path = Path.from(request.getPath());
        final String body = Files.readFile(path);

        return HttpResponse.ok(body)
                .contentType(getContentType(path));
    }
}
