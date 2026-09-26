package org.apache.coyote.http11.resolver;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import org.apache.coyote.http11.data.Request;
import org.apache.coyote.http11.data.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceResolver implements RequestResolver {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceResolver.class);

    private final String path;
    private final Charset charset;
    private final String charsetName;

    private StaticResourceResolver(
            final String path,
            final Charset charset
    ) {
        this.path = path;
        this.charset = charset;
        this.charsetName = charset.name().toLowerCase();
    }

    public static StaticResourceResolver create(
            final String path,
            final Charset charset
    ) {
        return new StaticResourceResolver(path, charset);
    }

    @Override
    public Response handleRequest(Request request) {
        final String resourcePath = path + request.getRequestPoint().getPath();

        try (var resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resourceStream == null) {
                return Response.notFound();
            }

            final byte[] resourceBytes = resourceStream.readAllBytes();
            final String responseBody = new String(resourceBytes, charset);

            return Response.ok(
                    Map.of("Content-Type", getContentType(resourcePath) + ";charset=" + charsetName),
                    responseBody
            );
        } catch (IOException e) {
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
    public boolean canHandle(Request request) {
        final String endpoint = request.getRequestPoint().getPath().toLowerCase();

        return endpoint.endsWith(".html")
                || endpoint.endsWith(".css")
                || endpoint.endsWith(".js");
    }

}
