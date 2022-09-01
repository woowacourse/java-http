package org.apache.coyote.http11.enums;

import java.io.File;
import java.io.IOException;
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
    ;

    private static final String ERROR_MESSAGE = "존재하지 않는 파일을 요청하였습니다. -> path: %s";

    private final String path;

    FilePath(final String path) {
        this.path = path;
    }

    public static FilePath of(final String path) {
        return Arrays.stream(FilePath.values())
                .filter(responseBody -> path.equals(responseBody.path))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format(ERROR_MESSAGE, path)));
    }

    public String generateFile() throws IOException {
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
