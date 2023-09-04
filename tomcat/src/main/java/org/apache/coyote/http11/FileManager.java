package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public class FileManager {

    private static final String PATH = "static";

    private final File file;

    public FileManager(File file) {
        this.file = file;
    }

    public static FileManager from(String url) {
        if (!url.contains(".")) {
            url = url + ".html";
        }
        if (!url.contains("/")) {
            url = "/" + url;
        }

        URL path = FileManager.class.getClassLoader().getResource(PATH + url);
        return new FileManager(new File(Objects.requireNonNull(path).getFile()));
    }

    public String mimeType() {
        String fileName = file.getName();
        String fileExtension = extractFileExtension(fileName);
        return mapMimeType(fileExtension);
    }

    private String extractFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
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

    public String fileContent() throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }

    public File file() {
        return file;
    }
}
