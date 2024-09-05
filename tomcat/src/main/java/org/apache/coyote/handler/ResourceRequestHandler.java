package org.apache.coyote.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.RequestHandler;

public class ResourceRequestHandler implements RequestHandler {

    private static final String STATIC_RESOURCE_BASE_URL = "static";
    private static final String DEFAULT_RESOURCE_EXTENSION = ".html";

    @Override
    public boolean canHandling(HttpRequest httpRequest) {
        return findResource(httpRequest.getPath()) != null;
    }

    @Override
    public String getContentType(HttpRequest httpRequest) {
        if (httpRequest.getHeaderValue("Accept") == null) {
            return "";
        }
        return httpRequest.getHeaderValue("Accept").split(",")[0];
    }

    @Override
    public String getResponseBody(HttpRequest httpRequest) throws IOException {
        URL resource = findResource(httpRequest.getPath());
        File file = new File(resource.getFile());
        Path path = file.toPath();
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    private URL findResource(String path) {
        URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCE_BASE_URL + path);
        if (resource == null) {
            resource = getClass().getClassLoader()
                    .getResource(STATIC_RESOURCE_BASE_URL + path + DEFAULT_RESOURCE_EXTENSION);
        }
        return resource;
    }
}
