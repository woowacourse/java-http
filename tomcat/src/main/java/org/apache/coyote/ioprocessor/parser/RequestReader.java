package org.apache.coyote.ioprocessor.parser;

import http.HttpMethod;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RequestReader {
    private static final String STATIC_RESOURCE_ROOT = "/static";
    private static final String ROOT_URI = "/";

    private final HttpMethod method;
    private final URI uri;
    private final String version;

    public RequestReader(String requestUri) throws URISyntaxException {
        String[] uriParts = requestUri.split(" ");
        validateUriParts(uriParts);
        this.uri = new URI(uriParts[1]);
        this.method = HttpMethod.nameOf(uriParts[0]);
        this.version = uriParts[2];
    }

    private void validateUriParts(String[] uriParts) {
        if (uriParts.length != 3) {
            throw new RuntimeException("failed to parse request uri");
        }
    }

    public boolean isRootUri() {
        return uri.getPath().equals(ROOT_URI);
    }

    public String readResource() throws IOException, URISyntaxException {
        URL staticResourceURL = getClass().getResource(STATIC_RESOURCE_ROOT + uri.getPath());
        Path path = Paths.get(staticResourceURL.toURI());
        return Files.readString(path);
    }

    public URI getUri() {
        return uri;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getVersion() {
        return version;
    }
}
