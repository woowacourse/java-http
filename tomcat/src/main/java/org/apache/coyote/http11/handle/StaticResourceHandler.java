package org.apache.coyote.http11.handle;

import java.nio.charset.Charset;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceHandler.class);

    private final String path;
    private final Charset charset;
    private final String charsetName;

    private StaticResourceHandler(
            final String path,
            final Charset charset
    ) {
        this.path = path;
        this.charset = charset;
        this.charsetName = charset.name().toLowerCase();
    }

    public static StaticResourceHandler create(
            final String path,
            final Charset charset
    ) {
        return new StaticResourceHandler(path, charset);
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        final String resourcePath = path + request.getRequestLine().getPath();

        try (var resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resourceStream == null) {
                response.notFound();
                return;
            }

            final byte[] resourceBytes = resourceStream.readAllBytes();
            final String responseBody = new String(resourceBytes, charset);

            response.setHeader("Content-Type", getContentType(resourcePath) + ";charset=" + charsetName);
            response.setBody(responseBody);
            return;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        throw new RuntimeException("Failed to read resource: " + resourcePath);
    }

    private String getContentType(String resourcePath) {
        if (resourcePath.endsWith(".html")) {
            return "text/html";
        } else if (resourcePath.endsWith(".css")) {
            return "text/css";
        } else if (resourcePath.endsWith(".js")) {
            return "text/javascript";
        }

        throw new IllegalArgumentException("Unsupported resource type: " + resourcePath);
    }

    @Override
    public boolean canHandle(HttpRequest request) {
        final String endpoint = request.getRequestLine().getPath().toLowerCase();

        return endpoint.endsWith(".html")
                || endpoint.endsWith(".css")
                || endpoint.endsWith(".js");
    }

}
