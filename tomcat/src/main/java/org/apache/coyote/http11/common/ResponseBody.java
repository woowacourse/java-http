package org.apache.coyote.http11.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class ResponseBody {

    private static final String STATIC_DIRECTORY = "static";

    private final String requestFile;
    private final String body;

    private ResponseBody(final String requestFile, final String body) {
        this.body = body;
        this.requestFile = requestFile;
    }

    public static ResponseBody from(final RequestURI requestURI) throws IOException {
        final var uri = requestURI.getUri();
        if("/".equals(uri)) {
            return new ResponseBody(requestURI.getUri(), "Hello world!");
        }

        return new ResponseBody(requestURI.getUri(), readFile(uri));
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

    public String requestFile() {
        return requestFile;
    }

}
