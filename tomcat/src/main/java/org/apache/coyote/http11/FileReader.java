package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader {

    private static final String PREFIX_STATIC_RESOURCES = "/static";

    public String readResourceFile(String filePath) throws URISyntaxException, IOException {
        URL resource = FileReader.class.getResource(PREFIX_STATIC_RESOURCES + filePath);
        if (resource == null) {
            throw new IllegalArgumentException("파일 경로가 유효하지 않습니다.");
        }
        return Files.readString(Paths.get(resource.toURI()));
    }
}
