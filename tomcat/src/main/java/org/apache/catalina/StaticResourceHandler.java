package org.apache.catalina;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class StaticResourceHandler {

    private static final String DEFAULT_RESOURCE_PATH = "/";
    private static final String DEFAULT_VALUE = "Hello world!";

    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException{
        String responsePath = httpRequest.path();
        if (httpResponse.headers().containsKey("Location")){
            responsePath = httpResponse.headers().get("Location");
        }

        final var responseBody = createResponseBody(responsePath);
        httpResponse.setBody(responseBody);
    }

    private byte[] createResponseBody(String responsePath) throws IOException {
        String resourcePath = getResourcePath(responsePath);

        if (responsePath.equals(DEFAULT_RESOURCE_PATH)) {
            return DEFAULT_VALUE.getBytes();
        }

        final URL resource = Objects.requireNonNull(
                getClass().getClassLoader().getResource(resourcePath));
        final Path path = new File(resource.getFile()).toPath();
        return Files.readAllBytes(path);
    }

    private String getResourcePath(String requestTarget) {
        String resourcePath = "static" + requestTarget;
        if (!requestTarget.contains(".")) {
            resourcePath = resourcePath.concat(".html");
        }
        return resourcePath;
    }

}
