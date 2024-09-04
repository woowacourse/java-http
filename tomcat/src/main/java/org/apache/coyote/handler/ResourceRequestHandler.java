package org.apache.coyote.handler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.RequestHandler;

public abstract class ResourceRequestHandler implements RequestHandler {

    private static final String STATIC_RESOURCE_BASE_URL = "static";

    @Override
    public String getResponseBody(HttpRequest httpRequest) throws IOException {
        String fileName = httpRequest.getRequestURI();
        URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCE_BASE_URL + fileName);
        File file = new File(resource.getFile());
        Path path = file.toPath();
        return Files.readString(path, StandardCharsets.UTF_8);
    }
}
