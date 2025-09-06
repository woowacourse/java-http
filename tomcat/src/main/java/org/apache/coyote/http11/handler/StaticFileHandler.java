package org.apache.coyote.http11.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.coyote.http11.exception.NotFoundException;
import org.apache.coyote.http11.message.HttpBody;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.ContentType;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;

public class StaticFileHandler implements HttpRequestHandler {

    private static final String STATIC_DIR = "static";

    @Override
    public boolean canHandle(HttpRequest request) {
        String path = request.getRequestPath();
        return getClass().getClassLoader().getResource(STATIC_DIR + path) != null;
    }

    @Override
    public HttpResponse handle(HttpRequest request) throws IOException {
        String path = request.getRequestPath();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(STATIC_DIR + path)) {

            validateInputStream(inputStream);

            byte[] content = inputStream.readAllBytes();

            HttpHeaders headers = HttpHeaders.fromLines(
                    List.of(
                            "Content-Type: " + ContentType.getMimeTypeFrom(path),
                            "Content-Length: " + content.length
                    )
            );

            return new HttpResponse(HttpStatus.OK, headers, HttpBody.from(content));
        }
    }

    private void validateInputStream(InputStream inputStream) {
        if (inputStream == null) {
            throw new NotFoundException();
        }
    }
}
