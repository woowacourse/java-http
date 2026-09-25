package org.apache.catalina.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ContentType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;

public class StaticResourceController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final var resource = findResource(request.getPath());
        if (resource == null) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setBody(ContentType.HTML, page("/404.html"));
            return;
        }

        response.setBody(ContentType.from(request.getPath()), readResource(resource));
    }

    private String readResource(final URL resource) throws IOException, URISyntaxException {
        return Files.readString(Path.of(resource.toURI()), UTF_8);
    }

    private URL findResource(final String path) {
        return getClass().getClassLoader().getResource("static" + path);
    }

    private String page(final String path) throws URISyntaxException, IOException {
        return readResource(findResource(path));
    }
}
