package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;

public enum ResponseBody {

    DEFAULT_PAGE("GET", "/"),
    INDEX_PAGE("GET", "/index.html"),
    INDEX_CSS("GET", "/css/styles.css"),
    ;

    private final String method;
    private final String path;

    ResponseBody(final String method, final String path) {
        this.method = method;
        this.path = path;
    }

    public static ResponseBody of(final String method, final String path) {
        return Arrays.stream(ResponseBody.values())
                .filter(responseBody -> method.equals(responseBody.method) && path.equals(responseBody.path))
                .findAny()
                .orElse(DEFAULT_PAGE);
    }

    public String generate() throws IOException {
        if (this == DEFAULT_PAGE) {
            return "Hello world!";
        }

        final File file = new File(Objects.requireNonNull(getClass()
                        .getClassLoader()
                        .getResource("static" + path))
                .getFile());

        return new String(Files.readAllBytes(file.toPath()));
    }
}
