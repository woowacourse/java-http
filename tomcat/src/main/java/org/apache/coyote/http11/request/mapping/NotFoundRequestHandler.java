package org.apache.coyote.http11.request.mapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class NotFoundRequestHandler implements RequestHandler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        final String notFoundHtmlPath = Objects.requireNonNull(getClass()
                .getClassLoader()
                .getResource("static" + "/" + "404.html")
        ).getPath();
        try {
            final String responseBody = new String(Files.readAllBytes(Path.of(notFoundHtmlPath)));
            return new HttpResponse(
                    ContentType.TEXT_HTML,
                    HttpStatus.NOT_FOUND,
                    new HttpHeaders(Map.of()),
                    responseBody
            );
        } catch (final IOException e) {
            throw new NoSuchElementException();
        }
    }
}
