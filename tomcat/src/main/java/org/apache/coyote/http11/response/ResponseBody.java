package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.coyote.http11.request.RequestURI;

public class ResponseBody {

    private static final String STATIC_DIRECTORY = "static";

    private final String body;

    private ResponseBody(final String body) {
        this.body = body;
    }

    public static ResponseBody from(final RequestURI requestURI) throws IOException {
        final var uri = requestURI.getUri();
        if("/".equals(uri)) {
            return new ResponseBody("Hello world!");
        }

        return new ResponseBody(readFile(uri));
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
