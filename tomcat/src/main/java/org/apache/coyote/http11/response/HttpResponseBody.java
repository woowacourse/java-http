package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.coyote.http11.request.HttpRequestURI;

public class HttpResponseBody {

    private static final String STATIC_DIRECTORY = "static";

    private final String body;

    private HttpResponseBody(final String body) {
        this.body = body;
    }

    public static HttpResponseBody from(final HttpRequestURI httpRequestURI) throws IOException {
        final var uri = httpRequestURI.getUri();
        if("/".equals(uri)) {
            return new HttpResponseBody("Hello world!");
        }

        return new HttpResponseBody(readFile(uri));
    }

    private static String readFile(final String uri) throws IOException {
        final var resource = ClassLoader.getSystemClassLoader().getResource(STATIC_DIRECTORY + uri);
        final var file = new File(Objects.requireNonNull(resource).getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }

    public int contentLength() {
        return this.body.getBytes().length;
    }

    public String body() {
        return body;
    }

}
