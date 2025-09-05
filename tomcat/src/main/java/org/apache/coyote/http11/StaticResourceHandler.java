package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.coyote.http11.response.Http11Response;

public class StaticResourceHandler {

    private static final String STATIC_PATH = "static";

    public void handleStaticResource(final Http11Response httpResponse)
            throws IOException, URISyntaxException {
        final String resourcePath = httpResponse.getResourcePath();
        if (resourcePath.equals("/")) {
            buildRootResponse(httpResponse);
            return;
        }
        final var resource = getClass().getClassLoader().getResource(STATIC_PATH + resourcePath);
        final var filePath = Paths.get(Objects.requireNonNull(resource).toURI());
        final var responseBody = Files.readAllBytes(filePath);
        httpResponse.addHeader("Content-Length", String.valueOf(responseBody.length));
        httpResponse.setBody(responseBody);
    }

    private void buildRootResponse(final Http11Response httpResponse) {
        final byte[] body = "Hello world!".getBytes();
        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(body.length));
        httpResponse.setBody(body);
    }
}
