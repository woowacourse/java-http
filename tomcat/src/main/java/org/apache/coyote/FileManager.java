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
        this.contents = findContents();
    }

    public byte[] getContent() {
        return contents;
    }

    public int getContentLength() {
        return contents.length;
    }

    private byte[] findContents() {
        byte[] contents;

        if (path.equals("/")) {
            contents = "Hello world!".getBytes(StandardCharsets.UTF_8);

            return contents;
        }

        try {
            final var url = getClass().getClassLoader().getResource(relativePath);
            if (url == null) {
                throw new IllegalArgumentException("Resource not found: " + relativePath);
            }
            Path absolutePath = Path.of(url.toURI());

            contents = Files.readAllBytes(absolutePath);

            return contents;

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to read resource: " + relativePath, e);
        }
    }
}
