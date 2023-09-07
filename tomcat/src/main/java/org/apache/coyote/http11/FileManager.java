package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.coyote.http11.response.HttpContentType;

public class FileManager {

    private static final String PATH = "static";
    private static final String EXTENSION_SIGN = ".";
    private static final String HTML_EXTENSION = ".html";
    private static final String PREFIX = "/";

    private final File file;

    public FileManager(File file) {
        this.file = file;
    }

    public static FileManager from(String location) {
        if (!location.contains(EXTENSION_SIGN)) {
            location = location + HTML_EXTENSION;
        }
        if (!location.startsWith(PREFIX)) {
            location = PREFIX + location;
        }
        URL path = FileManager.class.getClassLoader().getResource(PATH + location);

        validatePath(path);
        return new FileManager(new File(path.getFile()));
    }

    private static void validatePath(URL path) {
        if (path == null) {
            throw new IllegalArgumentException("존재하지 않는 파일의 경로입니다.");
        }
    }

    public HttpContentType decideContentType() {
        String fileName = file.getName();
        String fileExtension = extractFileExtension(fileName);
        return HttpContentType.getByFileExtension(fileExtension);
    }

    private String extractFileExtension(String fileName) {
        int extensionSignIndex = fileName.lastIndexOf(EXTENSION_SIGN);
        return fileName.substring(extensionSignIndex + 1);
    }

    private String mapMimeType(String fileExtension) {
        if (Objects.equals(fileExtension, "html")) {
            return "text/html";
        }
        if (Objects.equals(fileExtension, "css")) {
            return "text/css";
        }
        if (Objects.equals(fileExtension, "svg")) {
            return "image/svg+xml";
        }
        if (Objects.equals(fileExtension, "js")) {
            return "text/javascript";
        }
        return "text/plain";
    }

    public String fileContent() {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 불러올 수 없습니다.");
        }
    }

    public File file() {
        return file;
    }
}
