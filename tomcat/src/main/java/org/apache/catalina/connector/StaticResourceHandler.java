package org.apache.catalina.connector;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceHandler {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceHandler.class);

    private static final String STATIC_PATH = "static";
    private static final String RESOURCE_NOT_FOUND_PATH = "/404.html";

    public void handleStaticResource(final Http11Response httpResponse)
            throws URISyntaxException, IOException {
        final String resourcePath = httpResponse.getResourcePath();
        if (resourcePath.equals("/")) {
            buildRootResponse(httpResponse);
            return;
        }

        byte[] responseBody = getResponseBody(resourcePath, httpResponse);
        httpResponse.setBody(responseBody);
    }

    private byte[] getResponseBody(final String resourcePath, final Http11Response httpResponse)
            throws URISyntaxException, IOException {

        var resource = getResourceUrl(resourcePath);

        if (resource == null) {
            log.warn("Static Resource Not Found: {}", resourcePath);
            httpResponse.setStatusCode(404);
            resource = getResourceUrl(RESOURCE_NOT_FOUND_PATH);
        }

        final var filePath = Paths.get(Objects.requireNonNull(resource).toURI());
        return Files.readAllBytes(filePath);
    }

    private URL getResourceUrl(String resourcePath) {
        return getClass().getClassLoader().getResource(STATIC_PATH + resourcePath);
    }

    private void buildRootResponse(final Http11Response httpResponse) {
        final byte[] body = "Hello world!".getBytes();
        httpResponse.setBody(body);
    }
}
