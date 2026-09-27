package org.apache.catalina;

import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.enums.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class StaticResourceHandler {

    private static final String DEFAULT_RESOURCE_PATH = "/";
    private static final String DEFAULT_VALUE = "Hello world!";

    public void handle(String requestPath, HttpResponse httpResponse) throws IOException{
        if (requestPath.equals(DEFAULT_RESOURCE_PATH)) {
            httpResponse.setStatus(HttpStatus.OK);
            httpResponse.setBody(DEFAULT_VALUE.getBytes());
            return;
        }

        URL resource = getStaticPath(requestPath, httpResponse);
        final var responseBody = getResponseBody(resource);
        httpResponse.setBody(responseBody);
    }

    private URL getStaticPath(String requestPath, HttpResponse httpResponse){
        String resourcePath = "static" + requestPath;
        if (!requestPath.contains(".")) {
            resourcePath = resourcePath.concat(".html");
        }

        URL resource = getClass().getClassLoader().getResource(resourcePath);
        if (resource == null){
            resource = Objects.requireNonNull(
                    getClass().getClassLoader().getResource("static/404.html")
            );
            httpResponse.setStatus(HttpStatus.NOT_FOUND);
        }

        return  resource;
    }

    private byte[] getResponseBody(URL resource) throws IOException {
        final Path path = new File(resource.getFile()).toPath();
        return Files.readAllBytes(path);
    }

}
