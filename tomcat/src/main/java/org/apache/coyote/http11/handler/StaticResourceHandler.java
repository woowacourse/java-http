package org.apache.coyote.http11.handler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

public class StaticResourceHandler {

    private static final String DEFAULT_FILE_EXTENSION = ".html";
    private static final String STATIC_RESOURCE_PATH = "static";

    private final byte[] resource;
    private final String contentType;

    public StaticResourceHandler(String url) throws IOException, URISyntaxException {
        Path path = findPath(url);
        resource = Files.readAllBytes(path);
        contentType = URLConnection.guessContentTypeFromName(path.toString());
    }

    private Path findPath(String requestURL) throws FileNotFoundException, URISyntaxException {
        if (!requestURL.contains(".")) {
            requestURL += DEFAULT_FILE_EXTENSION;
        }

        URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCE_PATH + requestURL);
        if (resource == null) {
            throw new FileNotFoundException();
        }

        return Path.of(resource.toURI());
    }

    public byte[] getResource() {
        return resource;
    }

    public String getContentType() {
        return contentType;
    }
}
