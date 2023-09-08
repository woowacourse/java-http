package org.apache.coyote.FileReader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReader {

    private static final URL NOT_FOUND_HTML = FileReader.class.getResource("/static/404.html");
    private static final String DEFAULT_RESOURCE_PATH = "/static";

    private final Path path;
    private final boolean found;

    private FileReader(Path path, boolean found) {
        this.path = path;
        this.found = found;
    }

    public static FileReader from(String path) {
        try {
            URL resource = getResource(path);
            if (resource == null) {
                return new FileReader(Path.of(NOT_FOUND_HTML.toURI()), false);
            }
            return new FileReader(Path.of(resource.toURI()), true);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("파일 경로가 잘못됐습니다.");
        }
    }

    private static URL getResource(String path) {
        if (FileReader.class.getResource(DEFAULT_RESOURCE_PATH + path) == null) {
            return FileReader.class.getResource(DEFAULT_RESOURCE_PATH + path + ".html");
        }
        return FileReader.class.getResource(DEFAULT_RESOURCE_PATH + path);
    }

    public String read() {
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽을 수 없습니다.");
        }
    }

    public boolean isFound() {
        return found;
    }
}
