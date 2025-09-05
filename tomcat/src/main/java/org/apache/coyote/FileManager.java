package org.apache.coyote;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileManager {

    private final String relativePath;
    private byte[] contents;
    private String path;

    public FileManager(String relativePath, String path) {
        this.relativePath = relativePath;
        this.path = path;
        contents = findContents();
    }

    public String getContentString() {
        return new String(contents, StandardCharsets.UTF_8);
    }

    public int getContentLength() {
        return contents.length;
    }

    private byte[] findContents() {

        if (path.equals("/")) {
            return "Hello world!".getBytes();
        }

        try {
            final var url = getClass().getClassLoader().getResource(relativePath);
            if (url == null) {
                throw new IllegalArgumentException("Resource not found: " + relativePath);
            }
            Path absolutePath = Path.of(url.toURI());

            byte[] responseBody = Files.readAllBytes(absolutePath);
            return responseBody;

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to read resource: " + relativePath, e);
        }
    }
}
