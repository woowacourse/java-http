package org.apache.coyote.http11.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;

public enum FilePath {

    DEFAULT_PAGE("/"),
    INDEX_PAGE("/index.html"),
    INDEX_CSS("/css/styles.css"),
    INDEX_ASSETS_AREA("/assets/chart-area.js"),
    INDEX_ASSETS_BAR("/assets/chart-bar.js"),
    INDEX_ASSETS_PIE("/assets/chart-pie.js"),
    INDEX_JS("/js/scripts.js"),
    LOGIN_PAGE("/login.html"),
    FAVICON("/favicon.ico"),
    ;

    private static final char FILE_EXTENSION_SEPARATOR = '.';
    private static final String ERROR_MESSAGE = "존재하지 않는 파일을 요청하였습니다. -> path: %s";
    private static final String DEFAULT_PAGE_MESSAGE = "Hello world!";
    private static final String EMPTY_MESSAGE = "";
    private static final String STATIC_PATH = "static";

    private final String value;

    FilePath(final String value) {
        this.value = value;
    }

    public static FilePath of(final String path) {
        return Arrays.stream(FilePath.values())
                .filter(filePath -> filePath.value.contains(path))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format(ERROR_MESSAGE, path)));
    }

    public boolean isNotFilePath(final String path) {
        return !path.equals(value);
    }

    public String findFileExtension() {
        if (this == DEFAULT_PAGE) {
            return EMPTY_MESSAGE;
        }
        int index = value.lastIndexOf(FILE_EXTENSION_SEPARATOR) + 1;
        return value.substring(index);
    }

    public String generateFile() throws IOException {
        if (this == DEFAULT_PAGE) {
            return DEFAULT_PAGE_MESSAGE;
        }

        final URL url = Objects.requireNonNull(getClass()
                .getClassLoader()
                .getResource(STATIC_PATH + value));

        final File file = new File(url.getFile());

        return new String(Files.readAllBytes(file.toPath()));
    }
}
