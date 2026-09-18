package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.config.TomcatServerConfiguration.DEFAULT_CHARSET;
import static org.apache.coyote.http11.config.TomcatServerConfiguration.DEFAULT_CHARSET_NAME;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.coyote.http11.data.Request;
import org.apache.coyote.http11.data.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceRequestHandler implements RequestHandler {
    private static final Logger log = LoggerFactory.getLogger(StaticResourceRequestHandler.class);

    @Override
    public Response handle(Request request) {
        final String resourcePath = request.requestPoint().path();

        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("static" + resourcePath)) {
            if (resourceStream == null) {
                return Response.notFound();
            }

            final byte[] resourceBytes = resourceStream.readAllBytes();
            final String responseBody = new String(resourceBytes, DEFAULT_CHARSET);

            return Response.ok(
                    Map.of("Content-Type", getContentType(resourcePath) + ";charset=" + DEFAULT_CHARSET_NAME),
                    responseBody
            );
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        throw new RuntimeException("Failed to read resource: " + resourcePath);
    }

    @Override
    public boolean canHandle(Request request) {
        final String endpoint = request.requestPoint().path().toLowerCase();

        return endpoint.endsWith(".html")
                || endpoint.endsWith(".css")
                || endpoint.endsWith(".js");
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
}