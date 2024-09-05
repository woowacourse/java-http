package org.apache.coyote.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.RequestHandler;

public class ResourceRequestHandler implements RequestHandler {

    private static final String STATIC_RESOURCE_BASE_URL = "static";

    @Override
    public boolean canHandling(HttpRequest httpRequest) throws IOException {
        try {
            getResponseBody(httpRequest);
        } catch (AccessDeniedException | NullPointerException e) {
            return false;
        }
        return true;
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
        String fileName = httpRequest.getRequestURI();
        URL resource = findResource(fileName);
        File file = new File(resource.getFile());
        Path path = file.toPath();
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    private URL findResource(String uri) {
        URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCE_BASE_URL + uri);
        if (resource == null && uri.indexOf("?") > 0) {
            int index = uri.indexOf("?");
            String path = uri.substring(0, index);
            String queryString = uri.substring(index + 1);
            resource = getClass().getClassLoader().getResource(STATIC_RESOURCE_BASE_URL + path + ".html");
        }
        return resource;
    }
}
