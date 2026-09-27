package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StaticResourceRenderer {

    private static final String STATIC_RESOURCE_PREFIX = "static";

    public void writeResource(
            final String resourcePath,
            final HttpResponse response
    ) throws IOException {
        String fileName = STATIC_RESOURCE_PREFIX + resourcePath;
        byte[] responseBody;

        try (InputStream resource = getClass()
                .getClassLoader()
                .getResourceAsStream(fileName)) {
            if (resource == null) {
                response.setStatus(HttpStatus.NOT_FOUND);
                response.addHeader("Content-Type", "text/plain;charset=utf-8 ");
                response.setBody("Not Found".getBytes(StandardCharsets.UTF_8));
                return;
            }
            responseBody = resource.readAllBytes();
        }

        response.setStatus(HttpStatus.OK);
        response.addHeader("Content-Type", contentTypeOf(resourcePath));
        response.setBody(responseBody);
    }

    private String contentTypeOf(final String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }
        return "text/html;charset=utf-8 ";
    }
}
