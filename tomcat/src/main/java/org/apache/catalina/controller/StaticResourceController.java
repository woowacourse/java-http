package org.apache.catalina.controller;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class StaticResourceController implements Controller {

    private static final String STATIC_RESOURCE_ROOT = "static";
    private static final String NOT_FOUND_PAGE = "/404.html";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        var resourceUrl = findResource(request.getPath());
        if (resourceUrl == null) {
            response.setStatus(HttpStatus.NOT_FOUND);
            resourceUrl = findResource(NOT_FOUND_PAGE);
        }
        response.setBody(Files.readAllBytes(Path.of(resourceUrl.toURI())));
    }

    private URL findResource(final String path) {
        if (path.contains(".")) {
            return getClass().getClassLoader().getResource(STATIC_RESOURCE_ROOT + path);
        }
        return getClass().getClassLoader().getResource(STATIC_RESOURCE_ROOT + path + ".html");
    }
}
