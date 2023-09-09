package org.apache.coyote.controller.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public enum FileResolver {

    INDEX_HTML("index.html"),
    CSS("css/styles.css"),
    SCRIPTS_JS("js/scripts.js"),
    LOGIN("login.html"),
    REGISTER("register.html"),
    HTML_401("401.html"),
    ;

    private final String fileName;

    FileResolver(final String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public static String readFileToString(final String uri) throws IOException {
        return new String(Files.readAllBytes(findFilePath(uri)));
    }

    public static String readFileToString(final FileResolver file) throws IOException {
        final String uri = "/" + file.getFileName();
        return new String(Files.readAllBytes(findFilePath(uri)));
    }

    private static Path findFilePath(final String uri) {
        try {
            final URL url = ClassLoader.getSystemClassLoader().getResource("static" + uri);
            return new File(Objects.requireNonNull(url).getPath()).toPath();
        } catch (final NullPointerException exception) {
            throw new IllegalArgumentException("잘못된 파일 경로입니다.");
        }
    }
}
