package org.apache.coyote.http11.helper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader {

    private static final String PREFIX_STATIC_RESOURCES = "/static";

    private static FileReader INSTANCE;

    public static FileReader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FileReader();
        }
        return INSTANCE;
    }

    private FileReader() {
    }

    public String readResourceFile(String filePath) throws URISyntaxException, IOException {
        validateNull(filePath);
        URL resource = FileReader.class.getResource(PREFIX_STATIC_RESOURCES + filePath);
        validateResourceExistence(resource);
        return Files.readString(Paths.get(resource.toURI()));
    }

    private void validateNull(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("존재하지 않는 파일입니다.");
        }
    }

    private void validateResourceExistence(URL resource) {
        if (resource == null) {
            throw new IllegalArgumentException("존재하지 않는 파일입니다.");
        }
    }
}
