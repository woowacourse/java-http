package org.apache.coyote.http11.model;

import java.util.Arrays;
import org.apache.coyote.http11.utils.Files;

public enum Path {

    LOGIN("/login", "/login.html"),
    REGISTER("/register", "/register.html");

    private final String path;
    private final String fileName;

    Path(final String path, final String fileName) {
        this.path = path;
        this.fileName = fileName;
    }

    public static String from(final String path) {
        if (Files.existsFile(path)) {
            return path;
        }

        return Arrays.stream(Path.values())
                .filter(it -> it.path.equals(path))
                .findFirst()
                .map(it -> it.fileName)
                .orElseThrow(() -> new IllegalArgumentException("path가 잘못되었습니다. [path : " + path + "]"));
    }
}
