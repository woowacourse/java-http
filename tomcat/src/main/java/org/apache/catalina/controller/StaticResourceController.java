package org.apache.catalina.controller;

import static org.apache.coyote.http11.HttpConstants.CONTENT_LENGTH_HEADER;
import static org.apache.coyote.http11.HttpConstants.CONTENT_TYPE_HEADER;

import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.http11.dto.ContentType;
import org.apache.coyote.http11.dto.request.HttpRequest;
import org.apache.coyote.http11.dto.response.Status;

public class StaticResourceController implements Controller {

    private static final String STATIC_DIR = "static";

    @Override
    public ControllerResult service(final HttpRequest request) {
        final String path = request.requestLine().path().uri();
        return serve(path);
    }

    public ControllerResult serve(final String path) {
        final String resourcePath = STATIC_DIR + path;

        try (final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                return ControllerResult.of(Status.NOT_FOUND);
            }

            final byte[] body = inputStream.readAllBytes();
            final ContentType contentType = ContentType.fromPath(path);

            return ControllerResult.builder()
                    .status(Status.OK)
                    .header(CONTENT_TYPE_HEADER, contentType.value())
                    .header(CONTENT_LENGTH_HEADER, String.valueOf(body.length))
                    .body(new String(body))
                    .build();
        } catch (final IOException e) {
            return ControllerResult.of(Status.INTERNAL_ERROR);
        }
    }
}
