package org.apache.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;
import org.apache.util.PathUtils;

public class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest request) throws Exception {
        if (request.isGet()) {
            return doGet(request);
        }
        return doPost(request);
    }

    protected HttpResponse doPost(final HttpRequest request) throws Exception {
        return notFound();
    }

    protected HttpResponse doGet(final HttpRequest request) throws Exception {
        return notFound();
    }

    private HttpResponse notFound() throws IOException, URISyntaxException {
        final Path path = PathUtils.load("/404.html");
        final String responseBody = new String(Files.readAllBytes(path));
        return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.HTML, responseBody);
    }
}
