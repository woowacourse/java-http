package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader {

    private static final String PREFIX_STATIC_RESOURCES = "/static";
    private static final String DEFAULT_FILE_PATH = "/default.html";

    public static String readResourceFile() throws URISyntaxException, IOException {
        URL resource = FileReader.class.getResource(PREFIX_STATIC_RESOURCES + DEFAULT_FILE_PATH);
        if (resource == null) {
            throw new IllegalStateException("디폴트 파일 경로가 유효하지 않습니다.");
        }
        return Files.readString(Paths.get(resource.toURI()));
    }

    public static String readResourceFile(String filePath) throws URISyntaxException, IOException {
        URL resource = FileReader.class.getResource(PREFIX_STATIC_RESOURCES + filePath);
        if (resource == null) {
            return readResourceFile();
        }
        return Files.readString(Paths.get(resource.toURI()));
    }
}
