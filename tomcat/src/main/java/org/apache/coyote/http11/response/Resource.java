package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;

import utils.StringSplitter;

public class Resource {

    private static final String PATH_DELIMITER = "/";
    private static final String BASE_PATH = "static/";

    private final File file;
    private final String fileName;

    public Resource(final String filePath) {
        this.file = asFile(BASE_PATH + filePath);
        this.fileName = parseFileName(filePath);
    }

    private File asFile(final String filePath) {
        final String fullPath = asFullPath(filePath);
        return new File(fullPath);
    }

    private String asFullPath(final String filePath) {
        final URL resource = Optional.ofNullable(asURL(filePath))
                .orElseThrow(() -> new IllegalArgumentException("리소스를 찾을 수 없습니다 : " + filePath));
        return resource.getPath();
    }

    private URL asURL(final String filePath) {
        return getClass().getClassLoader()
                .getResource(filePath);
    }

    private String parseFileName(final String filePath) {
        return StringSplitter.getLast(PATH_DELIMITER, filePath);
    }

    public String read() {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (final IOException e) {
            throw new IllegalArgumentException("파일을 읽을 수 없습니다 : " + fileName);
        }
    }

    public String getName() {
        return StringSplitter.getLast(PATH_DELIMITER, fileName);
    }
}
