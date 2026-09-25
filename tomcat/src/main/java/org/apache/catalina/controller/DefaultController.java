package org.apache.catalina.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.catalina.util.StaticResourceResolver;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class DefaultController extends AbstractController {

    public DefaultController() {

    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        String contentType = request.requestHeader().resolveContentType();
        URL url = StaticResourceResolver.findStaticResource(request.startLine().path(), contentType);

        if (url == null) {
            return HttpResponse.status(HttpStatus.NOT_FOUND);
        }

        Path path = new File(url.getFile()).toPath();
        String responseBody = Files.readString(path);
        return HttpResponse.ok()
                .contentType(contentType)
                .body(responseBody);
    }
}
