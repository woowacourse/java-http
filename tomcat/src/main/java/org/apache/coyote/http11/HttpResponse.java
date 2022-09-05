package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.HttpStatus.OK;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class HttpResponse {

    private static final String DEFAULT_BODY = "Hello world!";

    private final String body;

    public HttpResponse(final String body) {
        this.body = body;
    }

    public static HttpResponse of(final Optional<URI> uri) throws URISyntaxException, IOException {
        if (uri.isEmpty()) {
            return defaultResponse();
        }
        final Path path = Paths.get(uri.get());
        final byte[] bytes = Files.readAllBytes(path);
        final String contentType = Files.probeContentType(path);
        final String response = makeResponse(contentType, bytes);

        return new HttpResponse(response);
    }

    private static String makeResponse(final String contentType, final byte[] bytes) {
        final String response = String.join("\r\n",
                "HTTP/1.1 " + OK.toStatusFormat(),
                CONTENT_TYPE.getValue() + ": " + contentType + ";charset=utf-8 ",
                CONTENT_LENGTH.getValue() + ": " + bytes.length + " ",
                "",
                new String(bytes));
        return response;
    }

    private static HttpResponse defaultResponse() {
        final String response = makeResponse("text/html", DEFAULT_BODY.getBytes());
        return new HttpResponse(response);
    }

    public String getBody() {
        return body;
    }
}
